package com.example.demo.dto.email;

import java.util.List;

public record EmailDto(
        String body,
        List<String> to,
        List<String> cc,
        List<String> cco,
        String subject,
        List<FileDto> attachments,
        EmailHtml emailHtml
) {
}
