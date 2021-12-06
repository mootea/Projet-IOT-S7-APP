package fr.cpe.pkmd.projet_iot_s7_app;

//Interface d'édcoute d'un changement de données
public interface Listen {
    void listen(String temp, String lum);
}
