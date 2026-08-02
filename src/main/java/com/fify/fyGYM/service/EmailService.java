package com.fify.fyGYM.service;

import com.fify.fyGYM.model.PanierItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ─── Ancien : code de vérification (gardé) ───────────────────────
    public void envoyerCodeVerification(String destinataire, String prenom, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinataire);
            helper.setFrom("Samikah23@gmail.com", "GymFy");
            helper.setSubject("GYMfy — Votre code de vérification");
            helper.setText(construireHtmlVerification(prenom, code), true);

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        }
    }

    // ─── Nouveau : facture après paiement ────────────────────────────
    /**
     * @param destinataire  email de l'acheteur
     * @param prenomNom     nom complet de l'acheteur
     * @param numeroFacture ID de la commande (Long)
     * @param produits      liste des PanierItem achetés
     * @param total         montant total en Ar
     * @param codeLivraison code unique à présenter au livreur
     */
    public void envoyerFacture(
            String destinataire,
            String prenomNom,
            Long numeroFacture,
            List<PanierItem> produits,
            long total,
            String codeLivraison
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinataire);
            helper.setFrom("Samikah23@gmail.com", "GymFy");
            helper.setSubject("GYMfy — Votre facture #" + numeroFacture);
            helper.setText(construireHtmlFacture(prenomNom, numeroFacture, produits, total, codeLivraison), true);

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de la facture : " + e.getMessage(), e);
        }
    }

    // ─── HTML Facture ─────────────────────────────────────────────────
    private String construireHtmlFacture(
            String prenomNom,
            Long numeroFacture,
            List<PanierItem> produits,
            long total,
            String codeLivraison
    ) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));

        // Lignes produits
        StringBuilder lignes = new StringBuilder();
        for (PanierItem item : produits) {
            long sousTotal = (long)(item.getQuantite() * item.getProduit().getPrix());
            lignes.append("""
                <tr>
                  <td style="padding:12px 16px; font-size:14px; color:#1e293b; border-bottom:1px solid #e2e8f0;">
                    %s
                  </td>
                  <td style="padding:12px 16px; font-size:14px; color:#64748b; text-align:center; border-bottom:1px solid #e2e8f0;">
                    %d
                  </td>
                  <td style="padding:12px 16px; font-size:14px; color:#64748b; text-align:right; border-bottom:1px solid #e2e8f0;">
                    %,.0f Ar
                  </td>
                  <td style="padding:12px 16px; font-size:14px; font-weight:700; color:#0f172a; text-align:right; border-bottom:1px solid #e2e8f0;">
                    %,.0f Ar
                  </td>
                </tr>
                """.formatted(
                    item.getProduit().getNom(),
                    item.getQuantite(),
                    (double) item.getProduit().getPrix(),
                    (double) sousTotal
            ));
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
                    <table role="presentation" width="560" cellpadding="0" cellspacing="0"
                           style="background-color:#ffffff; border-radius:18px; overflow:hidden; box-shadow:0 10px 30px rgba(11,22,41,0.12);">

                      <!-- HEADER -->
                      <tr>
                        <td style="background-color:#0b1629; padding:32px 40px;">
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td>
                                <span style="font-size:26px; font-weight:800; color:#e8edf5; letter-spacing:2px; text-transform:uppercase;">
                                  GYM<span style="color:#f59e0b;">fy</span>
                                </span>
                                <div style="font-size:11px; color:rgba(255,255,255,0.45); letter-spacing:3px; text-transform:uppercase; margin-top:4px;">
                                  Performance &amp; Nutrition
                                </div>
                              </td>
                              <td align="right">
                                <span style="font-size:12px; color:rgba(255,255,255,0.6);">Facture</span><br/>
                                <span style="font-size:20px; font-weight:800; color:#f59e0b;">#%d</span><br/>
                                <span style="font-size:11px; color:rgba(255,255,255,0.45);">%s</span>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- SALUTATION -->
                      <tr>
                        <td style="padding:32px 40px 16px;">
                          <p style="font-size:15px; color:#334155; margin:0; line-height:1.6;">
                            Bonjour <strong style="color:#0f172a;">%s</strong>,<br/>
                            Merci pour votre commande ! Voici le récapitulatif de votre achat.
                          </p>
                        </td>
                      </tr>

                      <!-- TABLEAU PRODUITS -->
                      <tr>
                        <td style="padding:0 40px 24px;">
                          <table width="100%%" cellpadding="0" cellspacing="0"
                                 style="border-radius:12px; overflow:hidden; border:1px solid #e2e8f0;">
                            <!-- En-tête tableau -->
                            <tr style="background-color:#0f2044;">
                              <td style="padding:12px 16px; font-size:12px; font-weight:700; color:#94a3b8; text-transform:uppercase; letter-spacing:1px;">
                                Produit
                              </td>
                              <td style="padding:12px 16px; font-size:12px; font-weight:700; color:#94a3b8; text-transform:uppercase; letter-spacing:1px; text-align:center;">
                                Qté
                              </td>
                              <td style="padding:12px 16px; font-size:12px; font-weight:700; color:#94a3b8; text-transform:uppercase; letter-spacing:1px; text-align:right;">
                                Prix unit.
                              </td>
                              <td style="padding:12px 16px; font-size:12px; font-weight:700; color:#94a3b8; text-transform:uppercase; letter-spacing:1px; text-align:right;">
                                Sous-total
                              </td>
                            </tr>
                            <!-- Lignes produits -->
                            %s
                            <!-- Total -->
                            <tr style="background-color:#f8fafc;">
                              <td colspan="3" style="padding:16px; font-size:15px; font-weight:700; color:#0f172a; text-align:right;">
                                TOTAL
                              </td>
                              <td style="padding:16px; font-size:18px; font-weight:800; color:#f59e0b; text-align:right;">
                                %,.0f Ar
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- CODE LIVRAISON -->
                      <tr>
                        <td style="padding:0 40px 32px;">
                          <table width="100%%" cellpadding="0" cellspacing="0"
                                 style="background-color:#0f2044; border-radius:14px; padding:24px; overflow:hidden;">
                            <tr>
                              <td style="text-align:center; padding:24px;">
                                <div style="font-size:12px; color:rgba(255,255,255,0.5); text-transform:uppercase; letter-spacing:2px; margin-bottom:12px;">
                                  🚚 Code de livraison
                                </div>
                                <div style="font-size:36px; font-weight:900; color:#fbbf24; letter-spacing:8px; font-family:'Courier New', monospace;">
                                  %s
                                </div>
                                <div style="font-size:12px; color:rgba(255,255,255,0.45); margin-top:12px; line-height:1.6;">
                                  Présentez ce code au livreur lors de la réception<br/>
                                  pour confirmer votre livraison.
                                </div>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- INFO -->
                      <tr>
                        <td style="padding:0 40px 32px; text-align:center;">
                          <p style="font-size:12.5px; color:#94a3b8; line-height:1.6; margin:0;">
                            Pour toute question, contactez-nous à<br/>
                            <a href="mailto:gymfy@email.mg" style="color:#3b82f6; text-decoration:none;">gymfy@email.mg</a>
                          </p>
                        </td>
                      </tr>

                      <!-- FOOTER -->
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
            """.formatted(
                numeroFacture, date,
                prenomNom,
                lignes.toString(),
                (double) total,
                codeLivraison
        );
    }

    // ─── HTML Vérification (ancien, gardé) ───────────────────────────
    private String construireHtmlVerification(String prenom, String code) {
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
                      <tr>
                        <td style="background-color:#0b1629; padding:32px 40px; text-align:center;">
                          <span style="font-size:26px; font-weight:800; color:#e8edf5; letter-spacing:2px; text-transform:uppercase;">
                            GYM<span style="color:#f59e0b;">fy</span>
                          </span>
                          <div style="font-size:11px; color:rgba(255,255,255,0.45); letter-spacing:3px; text-transform:uppercase; margin-top:6px;">
                            Performance &amp; Nutrition
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:40px 40px 24px; text-align:center;">
                          <div style="width:64px; height:64px; background-color:#c5d5f0; border-radius:50%%; margin:0 auto 20px; line-height:64px; font-size:28px;">📩</div>
                          <h1 style="font-size:24px; font-weight:800; color:#0f172a; text-transform:uppercase; letter-spacing:1px; margin:0 0 12px;">
                            Vérifiez votre adresse e-mail
                          </h1>
                          <p style="font-size:14px; color:#64748b; line-height:1.6; margin:0 0 28px;">
                            Bonjour <strong style="color:#0f172a;">%s</strong>,<br/>
                            Voici votre code de vérification GYMfy.
                          </p>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:0 40px 32px; text-align:center;">
                          <table role="presentation" align="center" cellpadding="0" cellspacing="0">
                            <tr>%s</tr>
                          </table>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:0 40px 36px; text-align:center;">
                          <p style="font-size:12.5px; color:#94a3b8; line-height:1.6; margin:0;">
                            ⏱️ Ce code est valable pendant <strong style="color:#64748b;">10 minutes</strong>.
                          </p>
                        </td>
                      </tr>
                      <tr>
                        <td style="background-color:#c8d2e4; padding:20px 40px; text-align:center;">
                          <p style="font-size:11px; color:#94a3b8; margin:0; letter-spacing:1px;">© 2026 GYMfy — Tous droits réservés</p>
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
