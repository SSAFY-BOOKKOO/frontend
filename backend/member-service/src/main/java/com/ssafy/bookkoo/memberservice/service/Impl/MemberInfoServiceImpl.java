package com.ssafy.bookkoo.memberservice.service.Impl;

import com.ssafy.bookkoo.memberservice.client.CommonServiceClient;
import com.ssafy.bookkoo.memberservice.dto.request.RequestMemberSettingDto;
import com.ssafy.bookkoo.memberservice.dto.request.RequestUpdateMemberInfoDto;
import com.ssafy.bookkoo.memberservice.dto.request.RequestUpdatePasswordDto;
import com.ssafy.bookkoo.memberservice.dto.response.ResponseMemberInfoDto;
import com.ssafy.bookkoo.memberservice.dto.response.ResponseMemberProfileDto;
import com.ssafy.bookkoo.memberservice.entity.Member;
import com.ssafy.bookkoo.memberservice.entity.MemberCategoryMapper;
import com.ssafy.bookkoo.memberservice.entity.MemberCategoryMapperKey;
import com.ssafy.bookkoo.memberservice.entity.MemberInfo;
import com.ssafy.bookkoo.memberservice.entity.MemberSetting;
import com.ssafy.bookkoo.memberservice.exception.MemberInfoNotExistException;
import com.ssafy.bookkoo.memberservice.exception.MemberNotFoundException;
import com.ssafy.bookkoo.memberservice.mapper.MemberInfoMapper;
import com.ssafy.bookkoo.memberservice.repository.MemberCategoryMapperRepository;
import com.ssafy.bookkoo.memberservice.repository.MemberInfoRepository;
import com.ssafy.bookkoo.memberservice.repository.MemberRepository;
import com.ssafy.bookkoo.memberservice.repository.MemberSettingRepository;
import com.ssafy.bookkoo.memberservice.service.MemberInfoService;
import com.ssafy.bookkoo.memberservice.service.MemberService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberInfoServiceImpl implements MemberInfoService {

    private final MemberInfoRepository memberInfoRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberInfoMapper memberInfoMapper;
    private final CommonServiceClient commonServiceClient;
    private final MemberSettingRepository memberSettingRepository;
    private final MemberCategoryMapperRepository memberCategoryMapperRepository;
    private final MemberService memberService;
    
    @Value("${config.member-bucket-name}")
    private String BUCKET;

    /**
     * 비밀번호를 업데이트합니다.
     * @param requestUpdatePasswordDto
     */
    @Override
    public void updatePassword(Long memberId, RequestUpdatePasswordDto requestUpdatePasswordDto) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(MemberNotFoundException::new);

        member.setPassword(passwordEncoder.encode(requestUpdatePasswordDto.password()));

        memberRepository.flush();
    }

    /**
     * 멤버 ID(UUID)를 통해 멤버 정보를 반환합니다. (마이페이지에 보여줄 내용만 반환)
     *
     * @param memberId
     * @return
     */
    @Override
    public ResponseMemberProfileDto getMemberProfileInfo(String memberId) {
        MemberInfo memberInfo = memberInfoRepository.findByMemberId(memberId)
                                                    .orElseThrow(MemberNotFoundException::new);
        String email = memberInfo.getMember()
                                 .getEmail();
        return memberInfoMapper.toResponseProfileDto(email, memberInfo);
    }

    @Override
    public ResponseMemberProfileDto getMemberProfileInfo(Long id) {
        MemberInfo memberInfo = memberInfoRepository.findById(id)
                                                    .orElseThrow(MemberNotFoundException::new);
        String email = memberInfo.getMember()
                                 .getEmail();
        return memberInfoMapper.toResponseProfileDto(email, memberInfo);
    }

    /**
     * 멤버 ID(Long)을 통해 멤버 정보를 반환합니다.
     * 멤버 정보 전체를 반환합니다.
     *
     * @param memberId
     * @return
     */
    @Override
    public ResponseMemberInfoDto getMemberInfo(Long memberId) {
        MemberInfo memberInfo = memberInfoRepository.findById(memberId)
                                                    .orElseThrow(MemberNotFoundException::new);

        return memberInfoMapper.toResponseDto(memberInfo);
    }

    /**
     * 내부적으로 사용하기 위한 서비스
     * 멤버 ID를 통해  PK(Long)을 반환하는 서비스
     * @param memberId
     * @return
     */
    @Override
    public Long getMemberPk(String memberId) {
        return memberInfoRepository.findByMemberId(memberId)
                                   .orElseThrow(MemberNotFoundException::new)
                                   .getId();
    }

    /**
     * @param followers
     * @return
     */
    @Override
    public List<Long> getRandomMemberInfo(List<Long> followers) {
        return memberInfoRepository.findRandomMemberInfoIdByFollowers(followers);
    }

    /**
     * 닉네임을 통해 memberId(Long)을 반환합니다.
     * @param nickName
     * @return
     */
    @Override
    public Long getMemberIdByNickName(String nickName) {
        return memberInfoRepository.findByNickName(nickName)
                                   .orElseThrow(MemberNotFoundException::new)
                                   .getId();
    }

    /**
     * 멤버의 공개 범위 설정 변경을 위한 서비스 로직
     * @param id
     * @param memberSettingDto
     */
    @Override
    @Transactional
    public void updateMemberSetting(Long id, RequestMemberSettingDto memberSettingDto) {
        MemberSetting memberSetting = memberSettingRepository.findById(id)
                                                             .orElseThrow(
                                                                 MemberNotFoundException::new);
        memberSetting.setIsLetterReceive(memberSettingDto.isLetterReceive());
        memberSetting.setReviewVisibility(memberSettingDto.reviewVisibility());
        memberSettingRepository.flush();
    }

    /**
     * 멤버 추가 정보를 변경하는 서비스 로직
     * @param id
     * @param memberInfoUpdateDto
     * @param profileImg
     */
    @Override
    @Transactional
    public void updateMemberInfo(Long id, RequestUpdateMemberInfoDto memberInfoUpdateDto,
        MultipartFile profileImg) {
        MemberInfo memberInfo = memberInfoRepository.findById(id)
                                                    .orElseThrow(MemberInfoNotExistException::new);
        checkDuplNickName(memberInfo.getNickName(), memberInfoUpdateDto.nickName());
        memberInfo.setNickName(memberInfoUpdateDto.nickName());
        if (profileImg != null) {
            updateProfileImg(id, profileImg);
        }
        updateCategories(memberInfo, memberInfoUpdateDto.categories());
        memberInfo.setIntroduction(memberInfoUpdateDto.introduction());
        memberInfoRepository.flush();
    }

    /**
     * 이전 닉네임과 같으면 중복체크 X
     * 다르면 중복 체크
     * @param oldNickName
     * @param newNickName
     */
    private void checkDuplNickName(String oldNickName, String newNickName) {
        if (!oldNickName.equals(newNickName)) {
            memberService.checkDuplNickName(newNickName);
        }
    }

    /**
     * 전체 카테고리 삭제 및 새로운 카테고리 저장
     * @param memberInfo
     * @param categories
     */
    @Transactional
    protected void updateCategories(MemberInfo memberInfo, Integer[] categories) {
        deleteCategories(memberInfo);
        saveCategories(memberInfo, categories);
    }

    /**
     * 전체 카테고리 삭제
     * @param memberInfo
     */
    private void deleteCategories(MemberInfo memberInfo) {
        memberCategoryMapperRepository.deleteAll(memberInfo.getCategories());
    }


    /**
     * 입력으로 들어온 카테고리를 저장
     * @param memberInfo
     * @param categories
     */
    private void saveCategories(MemberInfo memberInfo, Integer[] categories) {
        Arrays.stream(categories)
              .forEach((categoryId) -> {
                  //멤버 매퍼 키를 생성
                  MemberCategoryMapperKey memberCategoryMapperKey
                      = MemberCategoryMapperKey.builder()
                                               .categoryId(categoryId)
                                               .memberInfoId(memberInfo.getId())
                                               .build();
                  //매퍼키를 통해 매퍼 엔티티 생성
                  MemberCategoryMapper memberCategoryMapper
                      = MemberCategoryMapper.builder()
                                            .memberCategoryMapperKey(
                                                memberCategoryMapperKey)
                                            .memberInfo(memberInfo)
                                            .build();
                  //매퍼 테이블에 매퍼 정보 저장
                  MemberCategoryMapper categoryMapper = memberCategoryMapperRepository.save(
                      memberCategoryMapper);
                  //멤버 정보에 카테고리 연관관계 추가
                  memberInfo.addCategory(categoryMapper);
              });
    }

    /**
     * 새로운 이미지를 버킷에 저장하고 기본 이미지가 아니면 버킷에서 삭제하고 새로운 이미지 저장 멤버 정보의 프로필 사진 정보 수정
     *
     * @param id
     * @param profileImg
     */
    public void updateProfileImg(Long id, MultipartFile profileImg) {
        MemberInfo memberInfo = memberInfoRepository.findById(id)
                                                    .orElseThrow(MemberNotFoundException::new);
        String profileImgUrl = memberInfo.getProfileImgUrl();
        if (!profileImgUrl.equals("Default.jpg")) {
            commonServiceClient.deleteProfileImg(profileImgUrl, BUCKET);
        }
        String fileName = commonServiceClient.saveProfileImg(profileImg, BUCKET);
        memberInfo.setProfileImgUrl(fileName);
        memberInfoRepository.flush();
    }

}
