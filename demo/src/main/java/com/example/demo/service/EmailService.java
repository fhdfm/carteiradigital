package com.example.demo.service;

import com.example.demo.config.email.MailProperties;
import com.example.demo.dto.email.EmailDto;
import com.example.demo.dto.email.FileDto;
import com.example.demo.util.LogUtil;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.commons.codec.binary.Base64;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public EmailService(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    /**
     * Envia um email com base nos dados fornecidos.
     *
     * @param emailDto Dados do email a ser enviado
     */
    public void sendEmail(EmailDto emailDto) {
        LogUtil.info("┏● Preparando para enviar o email: {}");
        try {
            MimeMessage message = createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            setFrom(helper);
            setRecipients(helper, emailDto);
            setSubject(helper, emailDto.subject());
            setContent(helper, emailDto);
            addAttachments(helper, emailDto);

            send(message);
            LogUtil.info("┏● Email enviado com sucesso: {}", emailDto.subject());
        } catch (Exception e) {
            LogUtil.error("┏● Erro ao enviar email: {}\n┃Mensagem: {}\n┗●", emailDto.subject(), e.getMessage(), e);
        }
    }

    /**
     * Cria uma nova instância de MimeMessage.
     *
     * @return uma nova instância de MimeMessage
     */
    private MimeMessage createMimeMessage() {
        return mailSender.createMimeMessage();
    }

    /**
     * Configura o campo "From" do email.
     *
     * @param helper o auxiliar de mensagem MimeMessageHelper
     * @throws MessagingException em caso de erro ao configurar o campo
     */
    private void setFrom(MimeMessageHelper helper) throws MessagingException {
        try {
            helper.setFrom(mailProperties.getFrom());
            LogUtil.info("┏● Campo 'From' configurado\n┃Email: " + mailProperties.getFrom() + "\n┗●");
        } catch (MessagingException e) {
            LogUtil.error("┏● Erro ao configurar campo 'From'\n┃Mensagem: {}\n┗●", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Configura os destinatários (To, Cc, Bcc) do email.
     *
     * @param helper o auxiliar de mensagem MimeMessageHelper
     * @param emailDto os dados do email
     * @throws MessagingException em caso de erro ao configurar os destinatários
     */
    private void setRecipients(MimeMessageHelper helper, EmailDto emailDto) throws MessagingException {
        try {
            helper.setTo(emailDto.to().toArray(String[]::new));
            if (!CollectionUtils.isEmpty(emailDto.cc())) {
                helper.setCc(emailDto.cc().toArray(String[]::new));
            }
            if (!CollectionUtils.isEmpty(emailDto.cco())) {
                helper.setBcc(emailDto.cco().toArray(String[]::new));
            }
            LogUtil.info("┏● Destinatários configurados\n┃To: {}\n┃Cc: {}\n┃Bcc: {}\n┗●",
                    emailDto.to(), emailDto.cc(), emailDto.cco());
        } catch (MessagingException e) {
            LogUtil.error("┏● Erro ao configurar destinatários\n┃Mensagem: {}\n┗●", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Configura o assunto do email.
     *
     * @param helper o auxiliar de mensagem MimeMessageHelper
     * @param subject o assunto do email
     * @throws MessagingException em caso de erro ao configurar o assunto
     */
    private void setSubject(MimeMessageHelper helper, String subject) throws MessagingException {
        try {
            helper.setSubject(subject);
            LogUtil.info("┏● Assunto configurado\n┃Assunto: {}\n┗●", subject);
        } catch (MessagingException e) {
            LogUtil.error("┏● Erro ao configurar assunto\n┃Mensagem: {}\n┗●", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Configura o conteúdo HTML ou texto do email.
     *
     * @param helper o auxiliar de mensagem MimeMessageHelper
     * @param emailDto os dados do email
     * @throws MessagingException em caso de erro ao configurar o conteúdo
     */
    private void setContent(MimeMessageHelper helper, EmailDto emailDto) throws MessagingException {
        try {
            if (emailDto.emailHtml() != null) {
                String htmlContent = emailDto.emailHtml().htmlContent();
                String cssContent = emailDto.emailHtml().cssContent();

                boolean isHtmlWithEmbeddedCss = htmlContent.contains("<style>");

                if (!isHtmlWithEmbeddedCss && cssContent != null && !cssContent.isEmpty()) {
                    htmlContent = htmlContent.replaceFirst("<head>", "<head><style>\n" + cssContent + "\n</style>");
                }

                helper.setText(htmlContent, true);
            } else {
                helper.setText(emailDto.body(), true);
            }
            LogUtil.info("┏● Conteúdo configurado\n┃Conteúdo: {}\n┗●", emailDto.emailHtml() != null ? emailDto.emailHtml().htmlContent() : emailDto.body());
        } catch (MessagingException e) {
            LogUtil.error("┏● Erro ao configurar conteúdo\n┃Mensagem: {}\n┗●", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Adiciona anexos ao email.
     *
     * @param helper o auxiliar de mensagem MimeMessageHelper
     * @param emailDto os dados do email
     * @throws MessagingException em caso de erro ao adicionar os anexos
     */
    private void addAttachments(MimeMessageHelper helper, EmailDto emailDto) throws MessagingException {
        try {
            LogUtil.info("┏● Preparando anexos");
            if (!CollectionUtils.isEmpty(emailDto.attachments())) {
                for (FileDto fileDto : emailDto.attachments()) {
                    byte[] fileContent;
                    fileContent = Base64.decodeBase64(fileDto.file());
                    LogUtil.info("┃Anexo processado de base64\n┃FileName: {}\n┗●", fileDto.filename());
                    DataSource dataSource = new ByteArrayDataSource(fileContent, "application/octet-stream");
                    helper.addAttachment(fileDto.filename(), dataSource);
                }
            }
        } catch (MessagingException e) {
            LogUtil.error("┏● Erro ao adicionar anexos\n┃Mensagem: {}\n┗●", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Envia a mensagem de email.
     *
     * @param message a mensagem MimeMessage a ser enviada
     */
    private void send(MimeMessage message) {
        try {
            mailSender.send(message);
            LogUtil.info("┏● Email enviado\n┗●");
        } catch (Exception e) {
            LogUtil.error("┏● Erro ao enviar email\n┃Mensagem: {}\n┗●", e.getMessage(), e);
        }
    }
}
