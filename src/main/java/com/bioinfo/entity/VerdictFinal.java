package com.bioinfo.entity;

/**
 * Enum représentant les verdicts possibles de l'analyse génétique HBB.
 * Conforme au diagramme de classes BCE.
 */
public enum VerdictFinal {
    SAIN_AA, // Homozygote normal (pas de mutation)
    PORTEUR_AS, // Hétérozygote (porteur sain, une copie mutée)
    MALADE_SS // Homozygote muté (drépanocytose)
}
