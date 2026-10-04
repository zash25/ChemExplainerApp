package com.example.ui.screens.molecules

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

data class Atom3D(
    val id: Int,
    val element: String,
    val x: Float,
    val y: Float,
    val z: Float,
    val color: Color,
    val radiusRatio: Float = 1.0f,
    val electronegativity: Float,
    val valenceElectrons: Int,
    val atomicNumber: Int
)

data class Bond3D(
    val atom1Index: Int,
    val atom2Index: Int,
    val order: Int = 1, // 1 = single, 2 = double, 3 = triple, 4 = aromatic
    val isDashed: Boolean = false
)

data class MoleculeData(
    val id: String,
    val name: String,
    val chemicalFormula: String,
    val geometry: String,
    val bondAngle: String,
    val description: String,
    val atoms: List<Atom3D>,
    val bonds: List<Bond3D>,
    val scaleFactor: Float = 1.0f
)

object MoleculeRepository {

    // CPK standard element colors
    val ColorHydrogen = Color(0xFFF0F4F8)
    val ColorCarbon = Color(0xFF334155)
    val ColorOxygen = Color(0xFFEF4444)
    val ColorNitrogen = Color(0xFF3B82F6)
    val ColorChlorine = Color(0xFF10B981)
    val ColorSulfur = Color(0xFFFBBF24)
    val ColorCopper = Color(0xFFB45309)
    val ColorZinc = Color(0xFF64748B)

    val molecules = listOf(
        // 1. Water
        MoleculeData(
            id = "water",
            name = "Water",
            chemicalFormula = "H₂O",
            geometry = "Bent / V-shaped",
            bondAngle = "104.5°",
            description = "Polar molecule with two single covalent O-H bonds and two lone pairs on Oxygen. Creates strong intermolecular hydrogen bonds, giving water anomalous high boiling point.",
            atoms = listOf(
                Atom3D(0, "O", 0f, -0.2f, 0f, ColorOxygen, 1.2f, 3.5f, 6, 8),
                Atom3D(1, "H", -0.85f, 0.55f, 0f, ColorHydrogen, 0.7f, 2.1f, 1, 1),
                Atom3D(2, "H", 0.85f, 0.55f, 0f, ColorHydrogen, 0.7f, 2.1f, 1, 1)
            ),
            bonds = listOf(
                Bond3D(0, 1, 1),
                Bond3D(0, 2, 1)
            ),
            scaleFactor = 85f
        ),

        // 2. Methane
        MoleculeData(
            id = "methane",
            name = "Methane",
            chemicalFormula = "CH₄",
            geometry = "Tetrahedral (sp³)",
            bondAngle = "109.5°",
            description = "The simplest alkane. Carbon forms four equivalent sp³ hybrid single covalent bonds directed towards the corners of a regular tetrahedron.",
            atoms = listOf(
                Atom3D(0, "C", 0f, 0f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(1, "H", 0f, 1.15f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(2, "H", 1.08f, -0.38f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(3, "H", -0.54f, -0.38f, 0.94f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(4, "H", -0.54f, -0.38f, -0.94f, ColorHydrogen, 0.68f, 2.1f, 1, 1)
            ),
            bonds = listOf(
                Bond3D(0, 1, 1),
                Bond3D(0, 2, 1),
                Bond3D(0, 3, 1),
                Bond3D(0, 4, 1)
            ),
            scaleFactor = 75f
        ),

        // 3. Ethene
        MoleculeData(
            id = "ethene",
            name = "Ethene (Ethylene)",
            chemicalFormula = "C₂H₄",
            geometry = "Trigonal Planar (sp²)",
            bondAngle = "120°",
            description = "The first member of alkenes. Contains a rigid carbon-carbon double bond composed of one strong sigma bond and one exposed reactive pi bond, subject to electrophilic additions.",
            atoms = listOf(
                Atom3D(0, "C", -0.75f, 0f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(1, "C", 0.75f, 0f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(2, "H", -1.35f, 0.95f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(3, "H", -1.35f, -0.95f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(4, "H", 1.35f, 0.95f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(5, "H", 1.35f, -0.95f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1)
            ),
            bonds = listOf(
                Bond3D(0, 1, 2), // Double bond
                Bond3D(0, 2, 1),
                Bond3D(0, 3, 1),
                Bond3D(1, 4, 1),
                Bond3D(1, 5, 1)
            ),
            scaleFactor = 70f
        ),

        // 4. Ethyne
        MoleculeData(
            id = "ethyne",
            name = "Ethyne (Acetylene)",
            chemicalFormula = "C₂H₂",
            geometry = "Linear (sp)",
            bondAngle = "180°",
            description = "Unsaturated alkyne with a carbon-carbon triple bond (1 sigma + 2 pi bonds). Burns with oxygen to generate the high-temperature oxyacetylene flame (~3000°C) used in industrial welding.",
            atoms = listOf(
                Atom3D(0, "C", -0.65f, 0f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(1, "C", 0.65f, 0f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(2, "H", -1.75f, 0f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(3, "H", 1.75f, 0f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1)
            ),
            bonds = listOf(
                Bond3D(0, 1, 3), // Triple bond
                Bond3D(0, 2, 1),
                Bond3D(1, 3, 1)
            ),
            scaleFactor = 65f
        ),

        // 5. Benzene
        MoleculeData(
            id = "benzene",
            name = "Benzene",
            chemicalFormula = "C₆H₆",
            geometry = "Planar Hexagonal Aromatic",
            bondAngle = "120°",
            description = "Resonance-stabilized aromatic ring. The six carbon atoms share six delocalized pi electrons equally in a ring above and below the planar carbon framework.",
            atoms = buildList {
                val rC = 1.4f
                val rH = 2.45f
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60.0)).toFloat()
                    add(Atom3D(i, "C", rC * cos(angle), rC * sin(angle), 0f, ColorCarbon, 1.2f, 2.5f, 4, 6))
                }
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60.0)).toFloat()
                    add(Atom3D(6 + i, "H", rH * cos(angle), rH * sin(angle), 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1))
                }
            },
            bonds = listOf(
                Bond3D(0, 1, 4),
                Bond3D(1, 2, 4),
                Bond3D(2, 3, 4),
                Bond3D(3, 4, 4),
                Bond3D(4, 5, 4),
                Bond3D(5, 0, 4),
                Bond3D(0, 6, 1),
                Bond3D(1, 7, 1),
                Bond3D(2, 8, 1),
                Bond3D(3, 9, 1),
                Bond3D(4, 10, 1),
                Bond3D(5, 11, 1)
            ),
            scaleFactor = 50f
        ),

        // 6. Ethanol
        MoleculeData(
            id = "ethanol",
            name = "Ethanol",
            chemicalFormula = "C₂H₅OH",
            geometry = "Tetrahedral Carbons + Bent Oxygen",
            bondAngle = "109.5° / 104.5°",
            description = "Primary alkanol produced by fermentation of glucose. Contains polar hydroxyl group (-OH) capable of hydrogen bonding and undergoing esterification with carboxylic acids.",
            atoms = listOf(
                Atom3D(0, "C", -1.1f, 0f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(1, "C", 0.35f, 0.45f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(2, "O", 1.25f, -0.62f, 0f, ColorOxygen, 1.2f, 3.5f, 6, 8),
                Atom3D(3, "H", 2.1f, -0.3f, 0.1f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(4, "H", -1.25f, -1.05f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(5, "H", -1.55f, 0.45f, 0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(6, "H", -1.55f, 0.45f, -0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(7, "H", 0.5f, 1.05f, 0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(8, "H", 0.5f, 1.05f, -0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1)
            ),
            bonds = listOf(
                Bond3D(0, 1, 1),
                Bond3D(1, 2, 1),
                Bond3D(2, 3, 1),
                Bond3D(0, 4, 1),
                Bond3D(0, 5, 1),
                Bond3D(0, 6, 1),
                Bond3D(1, 7, 1),
                Bond3D(1, 8, 1)
            ),
            scaleFactor = 55f
        ),

        // 7. Ethanoic Acid
        MoleculeData(
            id = "ethanoic_acid",
            name = "Ethanoic Acid (Acetic Acid)",
            chemicalFormula = "CH₃COOH",
            geometry = "Planar Carboxyl Group",
            bondAngle = "120° (carbonyl) / 104.5° (hydroxyl)",
            description = "A monocarboxylic acid giving vinegar its sour taste and pungent smell. Contains both a carbonyl C=O and hydroxyl C-OH attached to the same carboxyl carbon.",
            atoms = listOf(
                Atom3D(0, "C", -1.2f, -0.3f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(1, "C", 0.25f, 0.1f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(2, "O", 0.65f, 1.25f, 0f, ColorOxygen, 1.2f, 3.5f, 6, 8), // Carbonyl
                Atom3D(3, "O", 1.05f, -0.95f, 0f, ColorOxygen, 1.2f, 3.5f, 6, 8), // Hydroxyl
                Atom3D(4, "H", 1.95f, -0.7f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(5, "H", -1.4f, -1.35f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(6, "H", -1.65f, 0.15f, 0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(7, "H", -1.65f, 0.15f, -0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1)
            ),
            bonds = listOf(
                Bond3D(0, 1, 1),
                Bond3D(1, 2, 2), // C=O double bond
                Bond3D(1, 3, 1), // C-OH single bond
                Bond3D(3, 4, 1), // O-H
                Bond3D(0, 5, 1),
                Bond3D(0, 6, 1),
                Bond3D(0, 7, 1)
            ),
            scaleFactor = 55f
        ),

        // 8. Glycine (Amino Acid)
        MoleculeData(
            id = "glycine",
            name = "Glycine (Amino Acid)",
            chemicalFormula = "NH₂-CH₂-COOH",
            geometry = "Bifunctional Zwitterion Precursor",
            bondAngle = "109.5° / 120°",
            description = "The simplest alpha-amino acid and building block of proteins. Forms peptide bonds (-CO-NH-) through condensation polymerization with release of water.",
            atoms = listOf(
                Atom3D(0, "N", -1.6f, -0.2f, 0f, ColorNitrogen, 1.2f, 3.0f, 5, 7),
                Atom3D(1, "C", -0.4f, 0.65f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(2, "C", 0.95f, -0.1f, 0f, ColorCarbon, 1.25f, 2.5f, 4, 6),
                Atom3D(3, "O", 1.2f, -1.25f, 0f, ColorOxygen, 1.2f, 3.5f, 6, 8),
                Atom3D(4, "O", 1.85f, 0.85f, 0f, ColorOxygen, 1.2f, 3.5f, 6, 8),
                Atom3D(5, "H", 2.65f, 0.5f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(6, "H", -2.4f, 0.25f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(7, "H", -1.65f, -1.15f, 0f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(8, "H", -0.45f, 1.3f, 0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1),
                Atom3D(9, "H", -0.45f, 1.3f, -0.88f, ColorHydrogen, 0.68f, 2.1f, 1, 1)
            ),
            bonds = listOf(
                Bond3D(0, 1, 1),
                Bond3D(1, 2, 1),
                Bond3D(2, 3, 2),
                Bond3D(2, 4, 1),
                Bond3D(4, 5, 1),
                Bond3D(0, 6, 1),
                Bond3D(0, 7, 1),
                Bond3D(1, 8, 1),
                Bond3D(1, 9, 1)
            ),
            scaleFactor = 50f
        )
    )
}
