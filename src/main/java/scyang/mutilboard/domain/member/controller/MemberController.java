package scyang.mutilboard.domain.member.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import scyang.mutilboard.domain.member.dto.MemberRequest;
import scyang.mutilboard.domain.member.dto.MemberResponse;
import scyang.mutilboard.domain.member.dto.MemberSearchCondition;
import scyang.mutilboard.domain.member.service.MemberService;
import scyang.mutilboard.global.common.ApiPageResponse;
import scyang.mutilboard.global.common.ApiResponse;
import scyang.mutilboard.global.common.MessageUtil;

import static scyang.mutilboard.global.common.MessageUtil.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("{memberId}")
    public ApiResponse<MemberResponse> getMember(@PathVariable("memberId") Long memberId){
        MemberResponse member = memberService.getMember(memberId);

        return ApiResponse.success(member, getMessage("board.search.success"));
    }

    @PutMapping("{memberId}")
    public ApiResponse<Void> updateMember(@PathVariable("memberId") Long memberId,
                                                          @RequestBody @Valid MemberRequest.Update request) {
        memberService.updateMember(memberId, request);

        return ApiResponse.success(null,getMessage("member.update.success"));
    }

    @GetMapping
    public ApiPageResponse<MemberResponse> getMemberList(
            @ModelAttribute MemberSearchCondition condition,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<MemberResponse> memberResponsePage
                = memberService.searchMembers(condition, pageable);

        return new ApiPageResponse<>(memberResponsePage, getMessage("board.search.success"));
    }


}
