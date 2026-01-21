package com.cybershield.protection.core.exception;

// exception opérée lorsqu'une politique de sécurité existe déjà pour une entreprise donnée
public class PolicyAlreadyExistsException extends RuntimeException {
    public PolicyAlreadyExistsException(String companyName) {
        super("Une politique de sécurité existe déjà pour l'entreprise : " + companyName);
    }
}