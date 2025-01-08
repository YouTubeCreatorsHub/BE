package com.creatorhub.platform.member.adapter.in.web;

import com.creatorhub.platform.member.adapter.in.web.dto.CreateMemberRequest;
import com.creatorhub.platform.member.adapter.in.web.dto.MemberResponse;
import com.creatorhub.platform.member.application.port.in.CreateMemberCommand;
import com.creatorhub.platform.member.application.service.MemberService;
import com.creatorhub.platform.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody CreateMemberRequest request) {
        CreateMemberCommand command = request.toCommand();
        Member member = memberService.createMember(command);
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable String id) {
        Member member = memberService.getMember(id);
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> responses = memberService.getAllMembers().stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}