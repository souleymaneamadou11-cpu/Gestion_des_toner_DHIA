/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import Model.Toner;
import Model.Utilisateur;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
/**
 *
 * @author DELL
 */
public class BonSortiePDF {
        private static final DeviceRgb DHIA_BLUE  = new DeviceRgb(0, 174, 239);
    private static final DeviceRgb DARK_BG    = new DeviceRgb(26, 29, 46);
    private static final DeviceRgb GREY_BG    = new DeviceRgb(245, 246, 250);
    private static final DeviceRgb TEXT_DARK  = new DeviceRgb(30, 30, 50);
    private static final DeviceRgb TEXT_GREY  = new DeviceRgb(110, 110, 135);
    private static final DeviceRgb BORDER_CLR = new DeviceRgb(210, 215, 230);
 
    /**
     * Génère le bon de sortie PDF au format DHIA.
     * @param numeroBon  Numéro unique du bon (BS-XXXXX)
     * @param user       Utilisateur qui fait la sortie
     * @param lignes     Map<Toner, Quantité>
     * @param departement Département demandeur
     * @return Chemin du fichier PDF généré
     */
    public static String generer(String numeroBon, Utilisateur user,
                                  Map<Toner, Integer> lignes,
                                  String departement) throws Exception {
 
        // Dossier de sortie dans Documents
        String dossier = System.getProperty("user.home") + File.separator + "Documents"
                       + File.separator + "BonsSortie";
        new File(dossier).mkdirs();
        String path = dossier + File.separator + "BonSortie_" + numeroBon + ".pdf";
 
        PdfWriter   writer   = new PdfWriter(path);
        PdfDocument pdfDoc   = new PdfDocument(writer);
        Document    document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(36, 40, 36, 40);
 
        PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont italic  = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
 
        // ═══════════════════════════════════════════════════
        // EN-TÊTE : Logo DHIA + titre
        // ═══════════════════════════════════════════════════
        Table enTete = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
        enTete.setWidth(UnitValue.createPercentValue(100));
        enTete.setMarginBottom(10);
 
        // Cellule logo DHIA
       /* Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setPaddingBottom(4);
        URL logoUrl = BonSortiePDF.class.getResource("resources/logo_Dhia.jpg");
        if (logoUrl != null) {
            Image logo = new Image(ImageDataFactory.create(logoUrl));
            logo.setWidth(110);
            logo.setAutoScaleHeight(true);
            logoCell.add(logo);
        } else {
            // Fallback texte si logo non trouvé
            Paragraph logoTxt = new Paragraph("DHIA")
                .setFont(bold).setFontSize(28)
                .setFontColor(DHIA_BLUE);
            logoCell.add(logoTxt);
        }*/
       Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setPaddingBottom(4);

// Essaye tous les chemins et noms possibles
    String[] cheminsPossibles = {
        "/resources/logo_Dhia.jpg",   // votre fichier exact
        "/resources/logo_dhia.jpg",   // minuscules
        "/resources/logo_Dhia.png",   // png majuscule
        "/resources/logo_dhia.png",   // png minuscules
        "/logo_Dhia.jpg",             // racine classpath
        "/logo_dhia.jpg",
    };

    Image logoImage = null;
    for (String chemin : cheminsPossibles) {
        try {
            java.io.InputStream is = BonSortiePDF.class.getResourceAsStream(chemin);
            if (is != null) {
                byte[] data = is.readAllBytes();
                is.close();
                logoImage = new Image(ImageDataFactory.create(data));
                //System.out.println("Logo trouvé : " + chemin); // confirmation dans console
                break;
            }
        } catch (Exception ignored) {}
    }

    if (logoImage != null) {
        //logoImage.setWidth(120);
        //logoImage.setAutoScaleHeight(true);
        logoImage.setWidth(90);  // réduit la largeur
        logoImage.setHeight(45); // hauteur fixe proportionnelle
        logoImage.setAutoScaleHeight(false);
        logoCell.add(logoImage);
    } else {
        // Fallback SVG-like avec iText si logo introuvable
        System.out.println("Logo introuvable — utilisation du texte DHIA");
        Paragraph logoTxt = new Paragraph("DHIA")
            .setFont(bold)
            .setFontSize(28)
            .setFontColor(DHIA_BLUE);
        logoCell.add(logoTxt);
    }
 
        // Cellule titre formulaire
        Cell titreCell = new Cell().setBorder(Border.NO_BORDER)
            .setVerticalAlignment(VerticalAlignment.BOTTOM);
        titreCell.add(new Paragraph("INTERNAL REQUEST FORM")
            .setFont(bold).setFontSize(16)
            .setFontColor(TEXT_DARK)
            .setTextAlignment(TextAlignment.RIGHT));
        titreCell.add(new Paragraph("Bon de sortie de matériel")
            .setFont(italic).setFontSize(10)
            .setFontColor(TEXT_GREY)
            .setTextAlignment(TextAlignment.RIGHT));
 
        enTete.addCell(logoCell);
        enTete.addCell(titreCell);
        document.add(enTete);
 
        // Ligne séparatrice bleue
        document.add(new LineSeparator(new SolidLine(2f) {{
            setColor(DHIA_BLUE);
        }}).setMarginBottom(10));
 
        // ═══════════════════════════════════════════════════
        // INFORMATIONS DU BON
        // ═══════════════════════════════════════════════════
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.setMarginBottom(14);
        infoTable.setBackgroundColor(GREY_BG);
        infoTable.setBorder(new SolidBorder(BORDER_CLR, 0.5f));
 
        addInfoCell(infoTable, "REQUEST NO :", numeroBon, regular, bold);
        addInfoCell(infoTable, "REQUEST DATE :", sdf.format(new Date()), regular, bold);
        addInfoCell(infoTable, "REQUESTED BY :", user.getPrenom() + " " + user.getNom(), regular, bold);
        addInfoCell(infoTable, "DEPARTMENT :", departement, regular, bold);
        document.add(infoTable);
 
        // ═══════════════════════════════════════════════════
        // TABLEAU DES LIGNES TONERS
        // ═══════════════════════════════════════════════════
        Table ligneTable = new Table(UnitValue.createPercentArray(new float[]{6, 36, 12, 14, 32}));
        ligneTable.setWidth(UnitValue.createPercentValue(100));
        ligneTable.setMarginBottom(14);
 
        // En-têtes du tableau
        String[] headers = {"N°", "DESCRIPTION", "UNIT", "QUANTITY", "LOCALISATION OF USE"};
        for (String h : headers) {
            Cell hCell = new Cell()
                .add(new Paragraph(h).setFont(bold).setFontSize(9).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(DARK_BG)
                .setPadding(6)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(BORDER_CLR, 0.5f));
            ligneTable.addHeaderCell(hCell);
        }
 
        // Lignes toners sélectionnés
        int numLigne = 1;
        for (Map.Entry<Toner, Integer> entry : lignes.entrySet()) {
            Toner t   = entry.getKey();
            int   qty = entry.getValue();
            String description = t.getMarque() + " " + t.getModele()
                + "\n" + t.getCouleur() + " — Réf : " + t.getReference();
            boolean pair = numLigne % 2 == 0;
            DeviceRgb rowBg = pair ? GREY_BG : new DeviceRgb(255, 255, 255);
 
            ligneTable.addCell(dataCell(String.valueOf(numLigne), regular, rowBg, TextAlignment.CENTER));
            ligneTable.addCell(dataCell(description, regular, rowBg, TextAlignment.LEFT));
            ligneTable.addCell(dataCell("Unité", regular, rowBg, TextAlignment.CENTER));
            ligneTable.addCell(dataCell(String.valueOf(qty), bold, rowBg, TextAlignment.CENTER));
            ligneTable.addCell(dataCell(departement, regular, rowBg, TextAlignment.LEFT));
            numLigne++;
        }
 
        // Lignes vides jusqu'à 20 pour remplir le formulaire
        while (numLigne <= 10) {
            DeviceRgb rowBg = numLigne % 2 == 0 ? GREY_BG : new DeviceRgb(255, 255, 255);
            for (int c = 0; c < 5; c++)
                ligneTable.addCell(dataCell(" ", regular, rowBg, TextAlignment.LEFT));
            numLigne++;
        }
        document.add(ligneTable);
 
        // ═══════════════════════════════════════════════════
        // SECTION SIGNATURES
        // ═══════════════════════════════════════════════════
        document.add(new Paragraph("SIGNATURES")
            .setFont(bold).setFontSize(10)
            .setFontColor(TEXT_GREY)
            .setMarginBottom(6));
 
        Table sigTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}));
        sigTable.setWidth(UnitValue.createPercentValue(100));
 
        String[][] sigHeaders = {
            {"REQUEST BY",        "Name – Surname – Signature"},
            {"CHIEF OF DEPARTMENT","Name – Surname – Signature"},
            {"LOGISTIC",          "Name – Surname – Signature"},
            {"RECEIVED BY",       "Name – Surname – Signature"}
        };
        for (String[] sh : sigHeaders) {
            Cell sc = new Cell()
                .add(new Paragraph(sh[0]).setFont(bold).setFontSize(9).setFontColor(TEXT_DARK))
                .add(new Paragraph(sh[1]).setFont(italic).setFontSize(8).setFontColor(TEXT_GREY))
                .setMinHeight(65)
                .setPadding(7)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setBorder(new SolidBorder(BORDER_CLR, 0.5f));
            sigTable.addCell(sc);
        }
        document.add(sigTable);
 
        // ═══════════════════════════════════════════════════
        // PIED DE PAGE
        // ═══════════════════════════════════════════════════
        document.add(new LineSeparator(new SolidLine(0.5f) {{ setColor(BORDER_CLR); }}).setMarginTop(10));
        document.add(new Paragraph(
            "Document généré automatiquement par GestionToners DHIA — " + sdf.format(new Date())
            + " — " + numeroBon)
            .setFont(italic).setFontSize(8)
            .setFontColor(TEXT_GREY)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(5));
 
        document.close();
        return path;
    }
 
    private static void addInfoCell(Table table, String label, String value, PdfFont regular, PdfFont bold) {
        Cell cell = new Cell()
            .add(new Paragraph(label).setFont(regular).setFontSize(8).setFontColor(TEXT_GREY))
            .add(new Paragraph(value).setFont(bold).setFontSize(10).setFontColor(TEXT_DARK))
            .setPadding(7)
            .setBorder(new SolidBorder(BORDER_CLR, 0.5f));
        table.addCell(cell);
    }
 
    private static Cell dataCell(String text, PdfFont font, DeviceRgb bg, TextAlignment align) {
        return new Cell()
            .add(new Paragraph(text).setFont(font).setFontSize(9).setFontColor(TEXT_DARK))
            .setBackgroundColor(bg)
            .setPadding(5)
            .setMinHeight(16)
            .setTextAlignment(align)
            .setBorder(new SolidBorder(BORDER_CLR, 0.5f));
    }
    
}
