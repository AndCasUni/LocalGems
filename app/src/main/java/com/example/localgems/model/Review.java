package com.example.localgems.model;

import java.sql.Timestamp;
import java.util.Date;

public class Review {
    private String utente;
    private String descrizione;
    private Long valutazione;
    private Date ora;

    public Review() {
        // Costruttore vuoto richiesto da Firestore
    }

    public Review(String utente, String descrizione, Long valutazione, Long ora) {
        this.utente = utente != null ? utente : "Anonimo";
        this.descrizione = descrizione != null ? descrizione : "Nessun contenuto";
        this.valutazione = valutazione != null ? valutazione : 0;
        this.ora = ora != null ? new Timestamp(ora) : new Timestamp(System.currentTimeMillis());
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Long getValutazione() {
        return valutazione;
    }

    public void setValutazione(Long valutazione) {
        this.valutazione = valutazione;
    }

    public Date getOra() {
        return ora;
    }

    public void setOra(Date ora) {
        this.ora = ora;
    }
}
