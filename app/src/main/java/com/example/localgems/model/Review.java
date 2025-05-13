package com.example.localgems.model;

import java.util.Date;

public class Review {
    private String descrizione;
    private String utente;
    private int valutazione;
    private Date ora;

    // Costruttore vuoto richiesto da Firestore
    public Review() {}


    // Costruttore opzionale per comodità
    public Review(String description, String user, int valuation, Date date) {
        this.descrizione = description;
        this.utente = user;
        this.valutazione = valuation;
        this.ora = date;

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

    public int getValutazione() {
        return valutazione;
    }

    public void setValutazione(int valutazione) {
        this.valutazione = valutazione;
    }

    public Date getOra() {
        return ora;
    }

    public void setOra(Date ora) {
        this.ora = ora;
    }
}
