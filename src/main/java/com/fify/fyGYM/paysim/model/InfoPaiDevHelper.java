package com.fify.fyGYM.paysim.model;

public class InfoPaiDevHelper {
    private String apiKey;
    private String idOrder;
    private java.math.BigDecimal totalprice;
    private String infoNumber;
    private String email;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getIdOrder() { return idOrder; }
    public void setIdOrder(String idOrder) { this.idOrder = idOrder; }

    public java.math.BigDecimal getTotalprice() { return totalprice; }
    public void setTotalprice(java.math.BigDecimal totalprice) { this.totalprice = totalprice; }

    public String getInfoNumber() { return infoNumber; }
    public void setInfoNumber(String infoNumber) { this.infoNumber = infoNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
