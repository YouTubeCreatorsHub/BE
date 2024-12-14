package com.creatorhub.application.community.comment.port.in;

import com.creatorhub.application.community.comment.dto.CommentCommand;
import com.creatorhub.application.community.comment.dto.CommentResponse;
import java.util.UUID;

public interface ManageCommentUseCase {
    CommentResponse createComment(CommentCommand.Create command);
    CommentResponse updateComment(CommentCommand.Update command);
    void deleteComment(UUID id);
}
