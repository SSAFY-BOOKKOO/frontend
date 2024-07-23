package com.ssafy.bookkoo.memberservice.mapper;

import com.ssafy.bookkoo.memberservice.dto.ResponseMemberInfoDto;
import com.ssafy.bookkoo.memberservice.entity.MemberInfo;
import com.ssafy.bookkoo.memberservice.service.MemberInfoService;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberInfoMapper {

    MemberInfoMapper INSTANCE = Mappers.getMapper(MemberInfoMapper.class);

    // 엔티티를 DTO로 변환
    ResponseMemberInfoDto toResponseDto(MemberInfo memberInfo);
}
