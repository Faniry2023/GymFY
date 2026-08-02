package com.fify.fyGYM.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService_v1 {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerCodeVerification(String destinataire, String prenom, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinataire);
            helper.setFrom("Samikah23@gmail.com","GymFy");
            helper.setSubject("GYMfy — Votre code de vérification");
            helper.setText(construireHtml(prenom, code), true);

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        }
    }

    private String construireHtml(String prenom, String code) {
        // On découpe le code en cases visuelles (ex: "1 2 3 4 5 6")
        StringBuilder codeBoxes = new StringBuilder();
        for (char c : code.toCharArray()) {
            codeBoxes.append("""
                <td style="width:46px; height:56px; background-color:#0f2044; border-radius:10px; text-align:center; vertical-align:middle;">
                    <span style="font-family:'Arial', sans-serif; font-size:28px; font-weight:800; color:#fbbf24; letter-spacing:0;">%s</span>
                </td>
                <td style="width:8px;"></td>
                """.formatted(c));
        }

        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            </head>
            <body style="margin:0; padding:0; background-color:#d4dbe8; font-family:Arial, Helvetica, sans-serif;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#d4dbe8; padding:40px 0;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:18px; overflow:hidden; box-shadow:0 10px 30px rgba(11,22,41,0.12);">

                      <!-- Header -->
                      <tr>
                        <td style="background-color:#0b1629; padding:32px 40px; text-align:center;">
                          <span style="font-family:Arial, sans-serif; font-size:26px; font-weight:800; color:#e8edf5; letter-spacing:2px; text-transform:uppercase;">
                            GYM<span style="color:#f59e0b;">fy</span>
                          </span>
                          <div style="font-size:11px; color:rgba(255,255,255,0.45); letter-spacing:3px; text-transform:uppercase; margin-top:6px;">
                            Performance &amp; Nutrition
                          </div>
                        </td>
                      </tr>

                      <!-- Body -->
                      <tr>
                        <td style="padding:40px 40px 24px; text-align:center;">
                          <div style="width:64px; height:64px; background-color:#c5d5f0; border-radius:50%%; margin:0 auto 20px; line-height:64px; font-size:28px;">
                            📩
                          </div>
                          <h1 style="font-family:Arial, sans-serif; font-size:24px; font-weight:800; color:#0f172a; text-transform:uppercase; letter-spacing:1px; margin:0 0 12px;">
                            Vérifiez votre adresse e-mail
                          </h1>
                          <p style="font-size:14px; color:#64748b; line-height:1.6; margin:0 0 28px;">
                            Bonjour <strong style="color:#0f172a;">%s</strong>,<br/>
                            Voici votre code de vérification GYMfy. Saisissez-le sur la page d'inscription pour activer votre compte.
                          </p>
                        </td>
                      </tr>

                      <!-- Code -->
                      <tr>
                        <td style="padding:0 40px 32px; text-align:center;">
                          <table role="presentation" align="center" cellpadding="0" cellspacing="0">
                            <tr>
                              %s
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- Info -->
                      <tr>
                        <td style="padding:0 40px 36px; text-align:center;">
                          <p style="font-size:12.5px; color:#94a3b8; line-height:1.6; margin:0;">
                            ⏱️ Ce code est valable pendant <strong style="color:#64748b;">10 minutes</strong>.<br/>
                            Si vous n'avez pas demandé ce code, vous pouvez ignorer cet e-mail.
                          </p>
                        </td>
                      </tr>

                      <!-- Footer -->
                      <tr>
                        <td style="background-color:#c8d2e4; padding:20px 40px; text-align:center;">
                          <p style="font-size:11px; color:#94a3b8; margin:0; letter-spacing:1px;">
                            © 2026 GYMfy — Tous droits réservés
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(prenom, codeBoxes.toString());
    }
}
