    package com.fify.fyGYM.model;

    import jakarta.persistence.*;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "publication")
    public class Publication {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Qui a publié
        @ManyToOne
        @JoinColumn(name = "utilisateur_id", nullable = false)
        private Utilisateur utilisateur;

        // Photo AVANT — stockée en bytes dans MySQL (LONGBLOB)
        @Lob
        @Column(columnDefinition = "LONGBLOB")
        private byte[] photoAvant;

        // Photo APRÈS — stockée en bytes dans MySQL (LONGBLOB)
        @Lob
        @Column(columnDefinition = "LONGBLOB")
        private byte[] photoApres;

        // Légende optionnelle
        private String legende;

        // Date de publication — initialisée automatiquement
        private LocalDateTime datePublication = LocalDateTime.now();

        // Liste des commentaires de cette publication
        // cascade = si on supprime la pub, on supprime ses commentaires
        @OneToMany(
                mappedBy  = "publication",
                cascade   = CascadeType.ALL,
                fetch     = FetchType.EAGER,
                orphanRemoval = true
        )
        private List<Commentaire> commentaires = new ArrayList<>();

        // Liste des réactions de cette publication
        // gérées dans la table Reaction séparément
        @OneToMany(
                mappedBy  = "publication",
                cascade   = CascadeType.ALL,
                fetch     = FetchType.LAZY,
                orphanRemoval = true
        )
        private List<Reaction> reactions = new ArrayList<>();


        // ════ GETTERS & SETTERS ════

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Utilisateur getUtilisateur() { return utilisateur; }
        public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

        public byte[] getPhotoAvant() { return photoAvant; }
        public void setPhotoAvant(byte[] photoAvant) { this.photoAvant = photoAvant; }

        public byte[] getPhotoApres() { return photoApres; }
        public void setPhotoApres(byte[] photoApres) { this.photoApres = photoApres; }

        public String getLegende() { return legende; }
        public void setLegende(String legende) { this.legende = legende; }

        public LocalDateTime getDatePublication() { return datePublication; }
        public void setDatePublication(LocalDateTime datePublication) { this.datePublication = datePublication; }

        public List<Commentaire> getCommentaires() { return commentaires; }
        public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }

        public List<Reaction> getReactions() { return reactions; }
        public void setReactions(List<Reaction> reactions) { this.reactions = reactions; }
    }