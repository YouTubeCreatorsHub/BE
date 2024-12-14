package com.creatorhub.application.community.common.validator;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class BusinessRuleValidator {

    public void validateEnabled(boolean isEnabled, String entityType) {
        if (!isEnabled) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("비활성화된 %s에는 접근할 수 없습니다.", entityType)
            );
        }
    }

    public void validateDeletion(long childCount, String entityType) {
        if (childCount > 0) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("하위 항목이 있는 %s는 삭제할 수 없습니다.", entityType)
            );
        }
    }

    public void validateCategoryAssignment(boolean isBoardEnabled, String categoryName) {
        if (!isBoardEnabled) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("비활성화된 게시판의 카테고리(%s)는 사용할 수 없습니다.", categoryName)
            );
        }
    }
}
