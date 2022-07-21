package com.jjambbong.note.service;

import com.jjambbong.note.entity.Member;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberService{

    public Long registerMember(Member member);

    public Optional<Member> findMember(Long memberId);

    public Member updateMember();

    public Long deleteMember();

}
