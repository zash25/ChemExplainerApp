package com.example.data.knowledge

data class ChemistryTopic(
    val id: String,
    val title: String,
    val level: String, // "SS1", "SS2", "SS3"
    val category: String, // "Fundamentals", "Periodic Trends", "Redox & Electrochemistry", "Organic Chemistry", "Biochemistry"
    val summary: String,
    val keyConcepts: List<String>,
    val formulasAndEquations: List<String>,
    val detailedNotes: String
)

data class QuizQuestion(
    val id: String,
    val topicId: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object ChemistryKnowledgeBase {

    val topics = listOf(
        ChemistryTopic(
            id = "intro_matter_separation",
            title = "Nature of Matter & Separation Techniques",
            level = "SS1",
            category = "Fundamentals",
            summary = "Three states of matter, particulate nature, physical vs chemical changes, and 12+ standard mixture separation techniques.",
            keyConcepts = listOf(
                "Matter: occupies space and has mass; solid, liquid, gas kinetic particle behavior",
                "Physical Change: easily reversible, no new substance (e.g. dissolution of salt, changes of state, sublimation)",
                "Chemical Change: irreversible, new substance formed with heat exchange (e.g. rusting, burning, addition of water to quicklime)",
                "Separation Techniques: Filtration, Centrifugation, Decantation, Evaporation, Crystallization, Fractional Crystallization, Precipitation, Sublimation, Distillation, Fractional Distillation, Separating Funnel, Paper Chromatography",
                "Criteria for Purity: Sharp melting and boiling points (impurities lower MP and raise BP); single spot on paper chromatogram"
            ),
            formulasAndEquations = listOf(
                "Sublimation: Solid + Heat -> Gas directly (Iodine, NH4Cl)",
                "Evaporation: Solution -> Solute (e.g., NaCl salt from brine)",
                "Fractional Distillation: for miscible liquids with close boiling points (< 100°C diff, e.g. ethanol 78°C and water 100°C, crude oil)"
            ),
            detailedNotes = """
                • Matter consists of solid (particles vibrate about fixed positions, fixed shape & volume), liquid (molecules slide, definite volume, shape of container), gas (negligible cohesive forces, rapid random motion, compressible).
                • Dalton's Atomic Theory (1808) & Modern Modifications:
                  1. Atoms are indivisible -> Modified: Protons, neutrons, electrons discovered (Rutherford/Thomson).
                  2. Atoms indestructible -> Modified: Nuclear fission breaks nuclei releasing energy.
                  3. Atoms of same element alike in mass -> Modified: Discovery of isotopes (e.g. Cl-35 and Cl-37).
                  4. Simple whole number ratios -> Holds mainly for inorganic compounds; large macromolecules (polymers, proteins) have thousands of atoms.
                • Separation Techniques:
                  - Separating Funnel: Immiscible liquids based on density (e.g., oil and water, kerosene and water).
                  - Centrifugation: Spinning suspensions at high speed to separate fine solids or blood cells from plasma.
                  - Chromatography: Solvent moving over porous adsorbent medium (paper). Used to identify dyes, blood drugs, petroleum fractions.
            """.trimIndent()
        ),
        ChemistryTopic(
            id = "elements_valency_orbitals",
            title = "Atomic Structure, Valency & Orbitals",
            level = "SS1",
            category = "Atomic Structure & Periodic Trends",
            summary = "Subatomic particles, atomic and mass numbers, isotopes, quantum numbers, Aufbau principle, Pauli exclusion, Hund's rule, and electronic configurations.",
            keyConcepts = listOf(
                "Constituents: Proton (+1, nucleus), Neutron (0, nucleus), Electron (-1, shells)",
                "Mass Number A = Protons (Z) + Neutrons; Relative Atomic Mass = weighted average of isotopic abundances",
                "Orbitals: Region of space where probability of finding electron is maximum (s is spherical, px, py, pz are dumbbell shaped)",
                "Quantum Numbers: n (principal shell, 1,2,3...), l (azimuthal/subsidiary, 0 to n-1: s,p,d,f), m (magnetic, -l to +l), s (spin, +1/2 or -1/2)",
                "Aufbau Principle: Electrons fill lowest energy orbitals first (1s < 2s < 2p < 3s < 3p < 4s < 3d...)",
                "Pauli Exclusion Principle: No two electrons in an atom can have the exact same 4 quantum numbers",
                "Hund's Rule of Maximum Multiplicity: Orbitals of equal energy (degenerate) are filled singly before pairing"
            ),
            formulasAndEquations = listOf(
                "Mass Number: A = Z + N",
                "Max electrons in energy shell n: 2n² (n=1: 2, n=2: 8, n=3: 18, n=4: 32)",
                "Total sub-shells/orbitals in shell n: n²",
                "RAM calculation: RAM = (Abundance1 × Mass1 + Abundance2 × Mass2) / 100",
                "Formula deduction from valency: Cross over valencies (e.g., Al³⁺ + O²⁻ -> Al₂O₃, Ca²⁺ + Cl⁻ -> CaCl₂)"
            ),
            detailedNotes = """
                • Electronic Configurations of First 20 Elements:
                  H(1s¹), He(1s²), Li(1s² 2s¹), Be(1s² 2s²), B(1s² 2s² 2p¹), C(1s² 2s² 2p²), N(1s² 2s² 2p³), O(1s² 2s² 2p⁴), F(1s² 2s² 2p⁵), Ne(1s² 2s² 2p⁶),
                  Na(1s² 2s² 2p⁶ 3s¹), Mg(..3s²), Al(..3s² 3p¹), Si(..3s² 3p²), P(..3s² 3p³), S(..3s² 3p⁴), Cl(..3s² 3p⁵), Ar(..3s² 3p⁶), K(..4s¹), Ca(..4s²).
                • Valency & Radicals: Ammonium (NH₄⁺, +1), Hydroxide (OH⁻, -1), Trioxonitrate(V) (NO₃⁻, -1), Trioxocarbonate(IV) (CO₃²⁻, -2), Tetraoxosulphate(VI) (SO₄²⁻, -2), Hydrogen trioxocarbonate (HCO₃⁻, -1).
            """.trimIndent()
        ),
        ChemistryTopic(
            id = "periodic_trends",
            title = "Periodicity & Periodic Trends",
            level = "SS2",
            category = "Atomic Structure & Periodic Trends",
            summary = "Mendeleev vs Moseley Modern Law, s/p/d/f blocks, atomic & ionic radius, ionization energy, electron affinity, and Pauling electronegativity.",
            keyConcepts = listOf(
                "Modern Periodic Law (Moseley): Physical and chemical properties are periodic functions of atomic number (Z), not atomic weight",
                "Periods (7 horizontal rows) & Groups (18 vertical columns); s-block (Gr 1-2), p-block (Gr 13-18), d-block (Gr 3-12 transition), f-block (lanthanoids & actinoids)",
                "Atomic Radius: Decreases across period (increased effective nuclear charge pulls electrons closer); Increases down group (addition of outer shells)",
                "Cation Radius is smaller than parent atom (fewer electrons, greater nuclear attraction); Anion Radius is larger (electron repulsion)",
                "Ionization Energy (IE): Energy to remove the most loosely bound electron from gaseous atom. Increases across period, decreases down group",
                "IE Exceptions: Be > B (2s² full shell penetration vs 2p¹); N > O (half-filled stable 2p³ vs 2p⁴ with pairing repulsion)",
                "Electron Affinity (Eea): Energy change when gaseous atom gains an electron. Exception: Cl is MORE negative than F due to small size of F causing intense 2p electron-electron repulsion",
                "Electronegativity: Atom's power in a molecule to attract bonding electrons (Pauling scale: F = 4.0 highest, decreases down and left)"
            ),
            formulasAndEquations = listOf(
                "Isoelectronic ions size order (10e⁻): Al³⁺ < Mg²⁺ < Na⁺ < F⁻ < O²⁻ < N³⁻ (higher nuclear charge = smaller radius)",
                "Group 1 alkali metals: 2M(s) + 2H₂O(l) -> 2MOH(aq) + H₂(g) (reactivity increases down group Li < Na < K < Rb < Cs)",
                "Group 17 halogens: Reactivity decreases down group F₂ > Cl₂ > Br₂ > I₂ (oxidizing power decreases)"
            ),
            detailedNotes = """
                • Periodic Trends Summary:
                  - Across Period (Left to Right): Nuclear charge increases, atomic size decreases, IE increases, Electronegativity increases, non-metallic character increases.
                  - Down Group (Top to Bottom): Atomic size increases, shielding increases, IE decreases, Electronegativity decreases, metallic character increases.
            """.trimIndent()
        ),
        ChemistryTopic(
            id = "redox_balancing",
            title = "Oxidation, Reduction & Redox Balancing",
            level = "SS2",
            category = "Redox & Electrochemistry",
            summary = "Oxidation numbers, classical vs electronic definitions (LEO GER / OIL RIG), half-reactions, and step-by-step ion-electron balancing in acid and base.",
            keyConcepts = listOf(
                "Oxidation: Loss of electrons / increase in oxidation number / addition of oxygen / removal of hydrogen",
                "Reduction: Gain of electrons / decrease in oxidation number / addition of hydrogen / removal of oxygen",
                "Oxidizing Agent (Oxidant): Gets reduced, causes oxidation in another species (e.g. KMnO₄, K₂Cr₂O₇, HNO₃, Cl₂)",
                "Reducing Agent (Reductant): Gets oxidized, causes reduction in another species (e.g. H₂S, Fe²⁺, SO₂/SO₃²⁻, metals)",
                "Oxidation Number Rules: Pure element = 0; Monatomic ion = charge; Fluorine = -1; Hydrogen = +1 (except hydrides NaH = -1); Oxygen = -2 (except peroxides H₂O₂ = -1, OF₂ = +2); Sum in neutral = 0, in polyatomic ion = ion charge",
                "Ion-Electron Balancing Steps: 1) Assign ON, 2) Split into half-reactions, 3) Balance key atoms, 4) Balance O with H₂O, 5) Balance H with H⁺ (or add OH⁻ in basic medium), 6) Balance charge with e⁻, 7) Equalize electrons and sum"
            ),
            formulasAndEquations = listOf(
                "LEO GER: Lose Electrons Oxidation, Gain Electrons Reduction",
                "Acidic Balancing Example: MnO₄⁻ + 8H⁺ + 5e⁻ -> Mn²⁺ + 4H₂O (Reduction)",
                "Oxidation half: 2I⁻ -> I₂ + 2e⁻",
                "Overall Acidic: 2MnO₄⁻ + 16H⁺ + 10I⁻ -> 2Mn²⁺ + 8H₂O + 5I₂",
                "Basic Conversion: Add equal OH⁻ to both sides to turn H⁺ into H₂O, then cancel waters: 2MnO₄⁻ + 8H₂O + 10I⁻ -> 2Mn²⁺ + 16OH⁻ + 5I₂",
                "Tests for Oxidizing Agents: FeCl₂ turns green to reddish-brown (Fe²⁺ -> Fe³⁺); Moist starch-iodide turns blue-black (I⁻ -> I₂); H₂S forms yellow sulfur deposit",
                "Tests for Reducing Agents: Purple KMnO₄ decolorizes (MnO₄⁻ -> Mn²⁺); Orange K₂Cr₂O₇ turns green (Cr₂O₇²⁻ -> Cr³⁺)"
            ),
            detailedNotes = """
                • Balancing in Basic Medium:
                  Follow all acidic steps using H⁺ and H₂O. Then, add OH⁻ to both sides equal to the number of H⁺ ions.
                  Combine H⁺ + OH⁻ -> H₂O on that side, then cancel common H₂O molecules on both sides.
            """.trimIndent()
        ),
        ChemistryTopic(
            id = "electrolysis_faraday",
            title = "Electrolysis, Cells & Faraday's Laws",
            level = "SS2",
            category = "Redox & Electrochemistry",
            summary = "Ionic theory, selective discharge, electrolytic vs galvanic cells, Daniell cell, salt bridge, industrial electrochemistry, and Faraday's calculations.",
            keyConcepts = listOf(
                "Ionic Theory (Arrhenius): Electrolytes dissociate into mobile ions in aqueous solution or molten state",
                "Electrolytic Cell: Consumes electrical energy for non-spontaneous chemical decomposition. Anode = positive, Cathode = negative",
                "Galvanic/Voltaic Cell: Produces electrical energy from spontaneous redox reaction. Anode = negative, Cathode = positive",
                "Daniell Cell: Zn(s) | Zn²⁺(1M) || Cu²⁺(1M) | Cu(s). Cell EMF E°cell = E°cathode - E°anode = 0.34 - (-0.76) = +1.10 V",
                "Salt Bridge (KCl/KNO₃ in agar-agar): Completes electrical circuit and maintains electrical neutrality by ion migration",
                "Preferential Discharge Factors: 1) Position in electrochemical series, 2) Concentration of ions, 3) Nature of electrodes (inert Pt/C vs active Cu)",
                "Faraday's 1st Law: Mass liberated m is directly proportional to quantity of electricity: m = Z × I × t",
                "Faraday's 2nd Law: Masses deposited by same current are proportional to chemical equivalents (Atomic weight / Valency)",
                "Faraday Constant: 1 Faraday = 96,500 Coulombs = charge on 1 mole of electrons (liberates 1 mol Ag⁺, 0.5 mol Cu²⁺, 0.33 mol Al³⁺)"
            ),
            formulasAndEquations = listOf(
                "Quantity of Electricity: Q = I × t (Coulombs = Amperes × Seconds)",
                "Mass Deposited: m = (Atomic Weight × I × t) / (Ionic Charge × 96500)",
                "Standard EMF: E°cell = E°reduction(cathode) - E°reduction(anode)",
                "Electrolysis of Brine: Cathode 2H⁺ + 2e⁻ -> H₂(g); Anode 2Cl⁻ -> Cl₂(g) + 2e⁻; NaOH left in solution",
                "Electrolysis of CuSO₄ (Pt electrodes): Cathode Cu deposited; Anode O₂ liberated (4OH⁻ -> 2H₂O + O₂ + 4e⁻); solution turns acidic (H₂SO₄)",
                "Electrolysis of CuSO₄ (Cu electrodes): Cathode Cu deposited; Anode Cu dissolves (Cu -> Cu²⁺ + 2e⁻); blue color unchanged (used in Cu refining!)",
                "H₂-O₂ Fuel Cell (Bacon Cell): 2H₂ + O₂ -> 2H₂O, 70-75% efficiency, zero pollution, used in space missions"
            ),
            detailedNotes = """
                • Selective Discharge in Aqueous Solution:
                  - Cations: K⁺, Ca²⁺, Na⁺, Mg²⁺, Al³⁺, Zn²⁺, Fe²⁺, Pb²⁺, H⁺, Cu²⁺, Ag⁺. (Ease of discharge increases down).
                  - Anions: SO₄²⁻, NO₃⁻, Cl⁻, Br⁻, I⁻, OH⁻. (Ease of discharge increases down).
                  - Overvoltage: In concentrated brine, Cl⁻ discharges at anode before OH⁻ despite lower position.
            """.trimIndent()
        ),
        ChemistryTopic(
            id = "hydrocarbons_organic",
            title = "Hydrocarbons: Alkanes, Alkenes, Alkynes & Benzene",
            level = "SS3",
            category = "Organic Chemistry",
            summary = "Saturated vs unsaturated aliphatic hydrocarbons, IUPAC nomenclature rules, isomerism types, addition & substitution mechanisms, benzene resonance and derivatives.",
            keyConcepts = listOf(
                "Alkanes (CnH2n+2): Saturated single bonds. Methane (CH₄) prep by heating sodium ethanoate with soda-lime. Substitution with Cl₂ under UV light",
                "Isomerism: Structural (chain, positional, functional) and Stereo (geometric cis/trans across C=C, and optical enantiomers)",
                "Alkenes (CnH2n): Double bond. Ethene (C₂H₄) prep by dehydration of ethanol with conc H₂SO₄ at 170°C, or cracking of petroleum",
                "Alkene Addition Reactions: Hydrogenation (+H₂, Ni catalyst), Halogenation (+Cl₂ -> 1,2-dichloroethane), Hydroxylation (alkaline KMnO₄ turns green -> ethane-1,2-diol), Hydration (+H₂O/H₂SO₄ -> ethanol), Polymerization to polythene",
                "Alkynes (CnH2n-2): Triple bond. Ethyne (C₂H₂) prep from cold water on calcium carbide (CaC₂ + 2H₂O -> Ca(OH)₂ + C₂H₂). Burns with hot oxyacetylene flame (3000°C) for welding",
                "Distinguishing Alkenes vs Alkynes: Alkynes form metallic dicarbide precipitates with ammoniacal CuCl (reddish-brown Cu₂C₂) and ammoniacal AgNO₃ (white Ag₂C₂)",
                "Benzene (C₆H₆): Planar aromatic ring with delocalized pi electrons. Undergoes electrophilic substitution (nitration with HNO₃/H₂SO₄, halogenation with FeCl₃, sulphonation, alkylation with AlCl₃)"
            ),
            formulasAndEquations = listOf(
                "Methane Chlorination: CH₄ + Cl₂ -> CH₃Cl + HCl -> CH₂Cl₂ -> CHCl₃ (chloroform) -> CCl₄",
                "Ethene Preparation: C₂H₅OH --(conc H₂SO₄, 170°C)--> C₂H₄ + H₂O",
                "Ethyne Preparation: CaC₂ + 2H₂O -> Ca(OH)₂ + C₂H₂",
                "Test for Unsaturation: Decolorization of reddish-brown bromine water (C₂H₄ + Br₂ -> CH₂Br-CH₂Br)",
                "Benzene Trimerization: 3C₂H₂ --(organo-nickel catalyst)--> C₆H₆ (benzene)",
                "Benzene Nitration: C₆H₆ + HNO₃ --(conc H₂SO₄, 55°C)--> C₆H₅NO₂ (nitrobenzene) + H₂O"
            ),
            detailedNotes = """
                • IUPAC Nomenclature:
                  1. Identify longest carbon chain (root: meth, eth, prop, but, pent, hex, hept, oct, non, dec).
                  2. Number from end nearest to functional group/multiple bond.
                  3. Designate substituents with position numbers, di-, tri-, tetra-, in alphabetical order.
            """.trimIndent()
        ),
        ChemistryTopic(
            id = "functional_biomolecules",
            title = "Alkanols, Acids, Esters, Soaps, Polymers & Biomolecules",
            level = "SS3",
            category = "Biochemistry",
            summary = "Alkanols (1°, 2°, 3°), fermentation of starch, alkanoic acids, esterification, fats/oils, saponification, detergents, addition/condensation polymers, carbohydrates, and proteins.",
            keyConcepts = listOf(
                "Alkanols (ROH): Primary (1 alkyl attached to C-OH), Secondary (2 alkyls), Tertiary (3 alkyls). Monohydric, Dihydric (glycol), Trihydric (glycerol)",
                "Oxidation of Alkanols: Primary -> Alkanal -> Alkanoic acid; Secondary -> Alkanone (ketone); Tertiary -> Not oxidized (no C-H on alpha carbon)",
                "Industrial Ethanol: Fermentation of starch (Starch --diastase--> Maltose --maltase--> Glucose --zymase--> Ethanol + CO₂)",
                "Alkanoic Acids (RCOOH): Weak organic acids. React with bases, carbonates (release CO₂), and electropositive metals (release H₂)",
                "Esterification: Reversible reaction between alkanol and alkanoic acid catalyzed by conc H₂SO₄ -> Ester (RCOOR') + H₂O. Sweet, fruity fragrances",
                "Fats and Oils: Esters of propane-1,2,3-triol (glycerol) with long-chain fatty acids (stearic, palmitic, oleic)",
                "Saponification: Alkaline hydrolysis of fats/oils with NaOH/KOH -> Soap (sodium octadecanoate) + Glycerol. Salted out with concentrated NaCl",
                "Detergent Mechanism: Hydrophobic nonpolar hydrocarbon tail dissolves in grease; Hydrophilic ionic head (-COO⁻ or -SO₃⁻) interacts with water, lifting grease into micelles",
                "Synthetic Detergents (Soapless ABS): Do not form insoluble scum in hard water (calcium/magnesium salts are soluble)",
                "Polymers: Addition (polythene, PVC, polypropene); Condensation with elimination of small molecule (nylon, terylene, proteins). Thermoplastics (remoldable) vs Thermosets (Bakelite, irreversible)",
                "Carbohydrates: Monosaccharides (Glucose reducing sugar, Fructose), Disaccharides (Sucrose non-reducing, Maltose, Lactose), Polysaccharides (Starch, Cellulose, Glycogen)",
                "Proteins: Polypeptide condensation of amino acids (peptide bond -CO-NH-). Tests: Millon's (white to brick-red on heating), Biuret (violet color with NaOH + CuSO₄), Xanthoproteic (intense yellow with conc HNO₃)"
            ),
            formulasAndEquations = listOf(
                "Esterification: CH₃COOH + C₂H₅OH <--(conc H₂SO₄)--> CH₃COOC₂H₅ + H₂O",
                "Saponification: (C₁₇H₃₅COO)₃C₃H₅ + 3NaOH -> 3C₁₇H₃₅COONa (Soap) + C₃H₅(OH)₃ (Glycerol)",
                "Glucose Combustion: C₆H₁₂O₆ + 6O₂ -> 6CO₂ + 6H₂O",
                "Fehling's Test for Glucose: Brick-red precipitate of Cu₂O on boiling with Fehling's solution A & B",
                "Amine / Amide: Methylamine (CH₃NH₂), Ethanamide (CH₃CONH₂), Urea / Carbamide (CO(NH₂)₂)"
            ),
            detailedNotes = """
                • Diagnostic Chemical Tests Summary:
                  - Unsaturation: Bromine water decolorizes (orange-brown to clear).
                  - Alkanol: Reacts with Na metal releasing H₂ gas; acidified KMnO₄ purple to colorless.
                  - Carboxylic acid: Effervescence of CO₂ with NaHCO₃/Na₂CO₃.
                  - Glucose: Brick-red ppt with Fehling's/Benedict's solution.
                  - Protein: Violet with Biuret test (NaOH + 1% CuSO₄); Brick-red with Millon's reagent; Yellow with conc HNO₃.
            """.trimIndent()
        )
    )

    val practiceQuestions = listOf(
        // Separation & Matter
        QuizQuestion(
            id = "q1",
            topicId = "intro_matter_separation",
            question = "Which of the following processes is a chemical change?",
            options = listOf("Melting of ice", "Liquefaction of air", "Slaking of lime (adding water to quicklime)", "Sublimation of iodine"),
            correctIndex = 2,
            explanation = "Slaking of lime (CaO + H₂O -> Ca(OH)₂) forms a new chemical substance (calcium hydroxide) with significant heat release, making it a chemical change."
        ),
        QuizQuestion(
            id = "q2",
            topicId = "intro_matter_separation",
            question = "Two immiscible liquids with different densities (e.g. kerosene and water) can be separated using:",
            options = listOf("Liebig condenser", "Separating funnel", "Centrifuge", "Fractionating column"),
            correctIndex = 1,
            explanation = "A separating funnel is specifically designed to separate immiscible liquids based on density difference; the denser liquid drains out first."
        ),
        QuizQuestion(
            id = "q3",
            topicId = "intro_matter_separation",
            question = "How does the presence of an impurity affect the melting point and boiling point of a pure substance?",
            options = listOf(
                "Raises melting point and lowers boiling point",
                "Lowers melting point and raises boiling point",
                "Both melting and boiling points increase",
                "No effect on either property"
            ),
            correctIndex = 1,
            explanation = "Impurities disrupt crystal lattice packing, lowering the melting point, and lower vapor pressure, thus elevating the boiling point."
        ),
        // Orbitals & Atomic Structure
        QuizQuestion(
            id = "q4",
            topicId = "elements_valency_orbitals",
            question = "What is the maximum number of electrons that can occupy the 4th energy level (N shell)?",
            options = listOf("8", "16", "32", "18"),
            correctIndex = 2,
            explanation = "The maximum number of electrons in an energy shell is given by 2n². For n = 4, 2(4)² = 2 × 16 = 32 electrons."
        ),
        QuizQuestion(
            id = "q5",
            topicId = "elements_valency_orbitals",
            question = "'In filling degenerate orbitals, electrons enter singly first before pairing up.' This principle is known as:",
            options = listOf("Aufbau Principle", "Pauli Exclusion Principle", "Hund's Rule of Maximum Multiplicity", "Dalton's Atomic Theory"),
            correctIndex = 2,
            explanation = "Hund's rule of maximum multiplicity states that electrons occupy degenerate orbitals singly with parallel spins before pairing begins."
        ),
        QuizQuestion(
            id = "q6",
            topicId = "elements_valency_orbitals",
            question = "Calculate the relative atomic mass of element X existing as 75% ³⁵Cl and 25% ³⁷Cl:",
            options = listOf("35.0", "35.5", "36.0", "36.5"),
            correctIndex = 1,
            explanation = "RAM = (75 × 35 + 25 × 37) / 100 = (2625 + 925) / 100 = 3550 / 100 = 35.5."
        ),
        // Periodic Trends
        QuizQuestion(
            id = "q7",
            topicId = "periodic_trends",
            question = "Why does Beryllium (Z=4) have a higher first ionization energy than Boron (Z=5)?",
            options = listOf(
                "Boron has a larger nuclear charge",
                "Beryllium has a stable completely filled 2s² orbital which penetrates closer to the nucleus",
                "Boron's 2p electron has lower shielding",
                "Beryllium is a non-metal"
            ),
            correctIndex = 1,
            explanation = "Beryllium has electronic configuration 1s² 2s² (fully filled s-orbital). Boron has 1s² 2s² 2p¹; removing the 2p electron is easier because 2s electrons shield it and 2s orbitals have higher penetration."
        ),
        QuizQuestion(
            id = "q8",
            topicId = "periodic_trends",
            question = "Which of the following isoelectronic ions has the smallest ionic radius?",
            options = listOf("O²⁻ (Z=8)", "F⁻ (Z=9)", "Na⁺ (Z=11)", "Al³⁺ (Z=13)"),
            correctIndex = 3,
            explanation = "All four ions have 10 electrons. Al³⁺ has the highest nuclear charge (+13 protons), pulling the 10 electrons closest to the nucleus, giving it the smallest ionic radius."
        ),
        QuizQuestion(
            id = "q9",
            topicId = "periodic_trends",
            question = "Why is the electron affinity of Chlorine (-348 kJ/mol) more negative than that of Fluorine (-328 kJ/mol)?",
            options = listOf(
                "Chlorine has higher electronegativity",
                "Fluorine has a very compact 2p subshell resulting in strong electron-electron repulsion",
                "Chlorine has fewer electron shells",
                "Fluorine is less reactive than chlorine"
            ),
            correctIndex = 1,
            explanation = "Fluorine's tiny 2p orbital creates significant inter-electronic repulsion when an incoming electron is added, making the energy released slightly less than in Chlorine's larger 3p orbital."
        ),
        // Redox Balancing
        QuizQuestion(
            id = "q10",
            topicId = "redox_balancing",
            question = "What is the oxidation number of Sulfur in the sulfate radical (SO₄²⁻)?",
            options = listOf("+4", "+2", "+6", "-2"),
            correctIndex = 2,
            explanation = "Let ON of S = x. Each oxygen is -2. Sum of charges: x + 4(-2) = -2 => x - 8 = -2 => x = +6."
        ),
        QuizQuestion(
            id = "q11",
            topicId = "redox_balancing",
            question = "When balancing the reduction half-reaction MnO₄⁻ -> Mn²⁺ in acidic solution, how many H⁺ ions and electrons are needed?",
            options = listOf("4H⁺ and 2e⁻", "8H⁺ and 5e⁻", "16H⁺ and 10e⁻", "8H⁺ and 2e⁻"),
            correctIndex = 1,
            explanation = "4 oxygens require 4 H₂O on the product side. 4 H₂O requires 8 H⁺ on the left. The charge on left is (-1 + 8) = +7, product is +2, so 5 electrons must be added: MnO₄⁻ + 8H⁺ + 5e⁻ -> Mn²⁺ + 4H₂O."
        ),
        QuizQuestion(
            id = "q12",
            topicId = "redox_balancing",
            question = "When acidified KMnO₄ acts as an oxidizing agent on a reducing agent, what color change occurs?",
            options = listOf("Orange to green", "Purple to colorless", "Green to reddish-brown", "Colorless to deep blue"),
            correctIndex = 1,
            explanation = "Purple potassium permanganate (MnO₄⁻ with Mn in +7 state) is reduced to colorless Mn²⁺ ions."
        ),
        // Electrolysis & Faraday
        QuizQuestion(
            id = "q13",
            topicId = "electrolysis_faraday",
            question = "During the electrolysis of concentrated sodium chloride solution (brine) with inert electrodes, what product is liberated at the cathode?",
            options = listOf("Sodium metal", "Chlorine gas", "Hydrogen gas", "Oxygen gas"),
            correctIndex = 2,
            explanation = "In aqueous solution, H⁺ is discharged in preference to Na⁺ at the cathode because H⁺ lies lower in the electrochemical series and has higher reduction potential: 2H⁺ + 2e⁻ -> H₂(g)."
        ),
        QuizQuestion(
            id = "q14",
            topicId = "electrolysis_faraday",
            question = "A steady current of 4.0 A is passed through CuSO₄ solution for 1800 seconds. Calculate the mass of copper deposited (Cu = 63.5, 1F = 96500 C):",
            options = listOf("1.2 g", "2.4 g", "4.8 g", "0.6 g"),
            correctIndex = 1,
            explanation = "Q = I × t = 4.0 × 1800 = 7200 C. Cu²⁺ + 2e⁻ -> Cu requires 2 × 96500 = 193000 C for 1 mol (63.5 g). Mass = (63.5 × 7200) / 193000 ≈ 2.37 g ≈ 2.4 g."
        ),
        QuizQuestion(
            id = "q15",
            topicId = "electrolysis_faraday",
            question = "In the Daniell cell (Zn | Zn²⁺ || Cu²⁺ | Cu), what is the function of the salt bridge?",
            options = listOf(
                "Supplies electrons to the outer wire",
                "Prevents current flow when the cell is active",
                "Maintains electrical neutrality and completes the circuit",
                "Dissolves copper atoms into ions"
            ),
            correctIndex = 2,
            explanation = "The salt bridge allows migration of inert ions (e.g. K⁺ and Cl⁻) to neutralize charge accumulation at both electrodes, completing the electrical circuit."
        ),
        // Organic Hydrocarbons
        QuizQuestion(
            id = "q16",
            topicId = "hydrocarbons_organic",
            question = "What reagent and conditions are used to prepare ethene from ethanol in the laboratory?",
            options = listOf(
                "Dilute HCl at room temperature",
                "Excess concentrated H₂SO₄ at 170°C",
                "Soda-lime with heat",
                "Nickel catalyst with hydrogen gas"
            ),
            correctIndex = 1,
            explanation = "Heating ethanol with excess concentrated H₂SO₄ at 170°C acts as a dehydrating agent to remove water, yielding ethene: C₂H₅OH -> C₂H₄ + H₂O."
        ),
        QuizQuestion(
            id = "q17",
            topicId = "hydrocarbons_organic",
            question = "Which test distinguishes an alkyne (like ethyne) from an alkene (like ethene)?",
            options = listOf(
                "Decolorization of bromine water",
                "Combustion in air",
                "Reaction with ammoniacal copper(I) chloride to form a reddish-brown precipitate",
                "Litmus paper test"
            ),
            correctIndex = 2,
            explanation = "Terminal alkynes like ethyne react with ammoniacal CuCl to form reddish-brown copper(I) dicarbide (Cu₂C₂), and with ammoniacal AgNO₃ to form white silver dicarbide. Alkenes do not form these precipitates."
        ),
        // Biomolecules & Polymers
        QuizQuestion(
            id = "q18",
            topicId = "functional_biomolecules",
            question = "What is the product formed when a secondary alkanol (e.g. propan-2-ol) is oxidized?",
            options = listOf("Alkanoic acid", "Alkanal (aldehyde)", "Alkanone (ketone)", "Alkene"),
            correctIndex = 2,
            explanation = "Secondary alkanols have two alkyl groups on the alpha carbon; oxidation removes two hydrogen atoms to form a ketone (alkanone) like propanone."
        ),
        QuizQuestion(
            id = "q19",
            topicId = "functional_biomolecules",
            question = "The cleansing action of soap is based on its molecule having:",
            options = listOf(
                "Two hydrophilic ends",
                "A hydrophobic hydrocarbon tail and a hydrophilic ionic head",
                "An acidic carboxyl group",
                "A non-polar aromatic ring only"
            ),
            correctIndex = 1,
            explanation = "The hydrophobic hydrocarbon tail dissolves in grease and oil, while the hydrophilic ionic head (-COO⁻Na⁺) interacts with water, forming micelles that lift dirt away."
        ),
        QuizQuestion(
            id = "q20",
            topicId = "functional_biomolecules",
            question = "Which chemical test yields a violet color confirming the presence of peptide bonds in proteins?",
            options = listOf("Millon's test", "Biuret test", "Fehling's test", "Seliwanoff's test"),
            correctIndex = 1,
            explanation = "The Biuret test (dilute NaOH followed by 1% CuSO₄ solution) reacts with peptide bonds (-CO-NH-) to produce a distinct violet/purple complex."
        )
    )

    fun searchCurriculum(query: String): String {
        val qLower = query.lowercase()
        val matchingTopics = topics.filter { topic ->
            topic.title.lowercase().contains(qLower) ||
            topic.summary.lowercase().contains(qLower) ||
            topic.keyConcepts.any { it.lowercase().contains(qLower) } ||
            topic.formulasAndEquations.any { it.lowercase().contains(qLower) } ||
            topic.detailedNotes.lowercase().contains(qLower)
        }

        if (matchingTopics.isEmpty()) {
            // Return top 2 primary topics as general context
            return topics.take(2).joinToString("\n\n") { topic ->
                "### Topic: ${topic.title} (${topic.level})\n${topic.summary}\nKey Concepts:\n" +
                topic.keyConcepts.joinToString("\n") { "• $it" } +
                "\nFormulas:\n" + topic.formulasAndEquations.joinToString("\n") { "• $it" }
            }
        }

        return matchingTopics.take(3).joinToString("\n\n") { topic ->
            "### Topic: ${topic.title} (${topic.level})\n${topic.summary}\nKey Concepts:\n" +
            topic.keyConcepts.joinToString("\n") { "• $it" } +
            "\nFormulas & Equations:\n" + topic.formulasAndEquations.joinToString("\n") { "• $it" } +
            "\nNotes:\n" + topic.detailedNotes
        }
    }
}
