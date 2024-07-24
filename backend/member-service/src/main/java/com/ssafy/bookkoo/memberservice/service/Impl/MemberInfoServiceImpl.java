package com.ssafy.bookkoo.memberservice.service.Impl;

import com.ssafy.bookkoo.memberservice.dto.RequestUpdatePasswordDto;
import com.ssafy.bookkoo.memberservice.dto.ResponseMemberInfoDto;
import com.ssafy.bookkoo.memberservice.entity.Member;
import com.ssafy.bookkoo.memberservice.entity.MemberInfo;
import com.ssafy.bookkoo.memberservice.exception.MemberNotFoundException;
import com.ssafy.bookkoo.memberservice.mapper.MemberInfoMapper;
import com.ssafy.bookkoo.memberservice.repository.MemberInfoRepository;
import com.ssafy.bookkoo.memberservice.repository.MemberRepository;
import com.ssafy.bookkoo.memberservice.service.MemberInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberInfoServiceImpl implements MemberInfoService {

    private final MemberInfoRepository memberInfoRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberInfoMapper memberInfoMapper = MemberInfoMapper.INSTANCE;

    @Override
    public void updatePassword(RequestUpdatePasswordDto requestUpdatePasswordDto) {
        Member member = memberRepository.findByMemberId(requestUpdatePasswordDto.memberId())
                                        .orElseThrow(MemberNotFoundException::new);
        member.setPassword(passwordEncoder.encode(requestUpdatePasswordDto.password()));

        memberRepository.save(member);
        memberRepository.flush();
    }

    //TODO: S3 연동 및 버켓에 이미지 저장 로직 구현
    @Override
    public void updateProfileImg(String memberId, MultipartFile profileImg) {
        MemberInfo memberInfo = memberInfoRepository.findByMemberId(memberId)
                                                    .orElseThrow(MemberNotFoundException::new);
    }

    @Override
    public ResponseMemberInfoDto getMemberInfo(String memberId) {
        MemberInfo memberInfo = memberInfoRepository.findByMemberId(memberId)
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
        return memberRepository.findByMemberId(memberId)
                                   .orElseThrow(MemberNotFoundException::new)
                                   .getId();
    }
}
