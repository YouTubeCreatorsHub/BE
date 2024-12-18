package com.creatorhub.presentation.common.error;

import com.creatorhub.application.community.member.dto.MemberCommand;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/business-exception")
    public void throwBusinessException() {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @GetMapping("/validation-exception")
    public void throwMethodArgumentNotValidException(@Valid @RequestBody Object request) {
        // Method body is not important as we're mocking the exception
    }

    @PostMapping("/validation-exception")
    public void throwMethodArgumentNotValidException(@RequestBody @Valid MemberCommand.Create command) {
        // validation 실패 시 자동으로 MethodArgumentNotValidException 발생
    }
}