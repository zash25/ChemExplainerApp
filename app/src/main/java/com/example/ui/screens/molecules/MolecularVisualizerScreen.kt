package com.example.ui.screens.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*

enum class VisualizerViewMode {
    BALL_AND_STICK,
    SPACE_FILLING,
    ORBITALS,
    DANIELL_CELL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MolecularVisualizerScreen() {
    var selectedMoleculeIndex by remember { mutableIntStateOf(0) }
    var viewMode by remember { mutableStateOf(VisualizerViewMode.BALL_AND_STICK) }
    var rotationX by remember { mutableFloatStateOf(20f) }
    var rotationY by remember { mutableFloatStateOf(35f) }
    var autoRotate by remember { mutableStateOf(true) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var selectedAtom by remember { mutableStateOf<Atom3D?>(null) }
    var showInfoSheet by remember { mutableStateOf(false) }

    val molecule = MoleculeRepository.molecules[selectedMoleculeIndex]

    // Auto-rotate coroutine
    LaunchedEffect(autoRotate) {
        while (autoRotate) {
            rotationY = (rotationY + 0.75f) % 360f
            delay(16) // ~60fps
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "3D Molecular Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Interactive Molecular Structure & Orbitals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { autoRotate = !autoRotate },
                        modifier = Modifier.testTag("toggle_auto_rotate")
                    ) {
                        Icon(
                            imageVector = if (autoRotate) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Auto-Rotation",
                            tint = if (autoRotate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            rotationX = 20f
                            rotationY = 35f
                            zoomScale = 1.0f
                        },
                        modifier = Modifier.testTag("reset_view_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Angle")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mode Selector Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = viewMode == VisualizerViewMode.BALL_AND_STICK,
                    onClick = { viewMode = VisualizerViewMode.BALL_AND_STICK },
                    label = { Text("Ball & Stick", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("chip_ball_stick")
                )
                FilterChip(
                    selected = viewMode == VisualizerViewMode.SPACE_FILLING,
                    onClick = { viewMode = VisualizerViewMode.SPACE_FILLING },
                    label = { Text("Space-Filling", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Circle, null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("chip_space_filling")
                )
                FilterChip(
                    selected = viewMode == VisualizerViewMode.ORBITALS,
                    onClick = { viewMode = VisualizerViewMode.ORBITALS },
                    label = { Text("Orbitals (s/p)", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("chip_orbitals")
                )
                FilterChip(
                    selected = viewMode == VisualizerViewMode.DANIELL_CELL,
                    onClick = { viewMode = VisualizerViewMode.DANIELL_CELL },
                    label = { Text("Galvanic Cell", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Bolt, null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("chip_daniell_cell")
                )
            }

            // Molecule Selector (for standard molecule modes)
            if (viewMode == VisualizerViewMode.BALL_AND_STICK || viewMode == VisualizerViewMode.SPACE_FILLING) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MoleculeRepository.molecules.indices.toList()) { index ->
                        val item = MoleculeRepository.molecules[index]
                        val isSelected = index == selectedMoleculeIndex
                        ElevatedAssistChip(
                            onClick = {
                                selectedMoleculeIndex = index
                                selectedAtom = null
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        item.chemicalFormula,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            colors = AssistChipDefaults.elevatedAssistChipColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("molecule_chip_$index")
                        )
                    }
                }
            }

            // Main Interactive Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0A0F1D))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            autoRotate = false
                            rotationY = (rotationY + dragAmount.x * 0.5f) % 360f
                            rotationX = (rotationX - dragAmount.y * 0.5f).coerceIn(-85f, 85f)
                        }
                    }
                    .testTag("molecular_3d_canvas"),
                contentAlignment = Alignment.Center
            ) {
                when (viewMode) {
                    VisualizerViewMode.BALL_AND_STICK,
                    VisualizerViewMode.SPACE_FILLING -> {
                        MoleculeCanvas(
                            molecule = molecule,
                            rotationX = rotationX,
                            rotationY = rotationY,
                            zoomScale = zoomScale,
                            isSpaceFilling = viewMode == VisualizerViewMode.SPACE_FILLING,
                            onAtomClicked = { atom ->
                                selectedAtom = atom
                                showInfoSheet = true
                            }
                        )
                    }
                    VisualizerViewMode.ORBITALS -> {
                        OrbitalsCanvas(
                            rotationX = rotationX,
                            rotationY = rotationY,
                            zoomScale = zoomScale
                        )
                    }
                    VisualizerViewMode.DANIELL_CELL -> {
                        DaniellCellCanvas(
                            zoomScale = zoomScale
                        )
                    }
                }

                // Drag indicator badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = Color.Cyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Drag to rotate 3D",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Zoom controls overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledIconButton(
                        onClick = { zoomScale = (zoomScale + 0.15f).coerceAtMost(2.2f) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    FilledIconButton(
                        onClick = { zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.5f) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Chemical Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("molecule_info_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    when (viewMode) {
                        VisualizerViewMode.BALL_AND_STICK,
                        VisualizerViewMode.SPACE_FILLING -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${molecule.name} (${molecule.chemicalFormula})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Angle: ${molecule.bondAngle}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Geometry: ${molecule.geometry}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = molecule.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        VisualizerViewMode.ORBITALS -> {
                            Text(
                                text = "Atomic Orbitals & Electron Probability Clouds",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• s-orbital: Spherically symmetrical (l = 0). Electron density depends only on distance r from nucleus.\n" +
                                       "• p-orbitals: Three dumbbell-shaped degenerate orbitals (px, py, pz) oriented along orthogonal Cartesian axes with a central nodal plane (electron density = 0 at nucleus).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        VisualizerViewMode.DANIELL_CELL -> {
                            Text(
                                text = "Daniell Voltaic Cell (Zn | Zn²⁺ || Cu²⁺ | Cu)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Zinc Anode (-): Zn(s) → Zn²⁺(aq) + 2e⁻ (Oxidation)\n" +
                                       "• Copper Cathode (+): Cu²⁺(aq) + 2e⁻ → Cu(s) (Reduction)\n" +
                                       "• Salt Bridge: Inverted U-tube with KCl/agar maintaining electrical neutrality. EMF = +1.10 V",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3D Canvas Projection & Molecule Rendering
// ----------------------------------------------------

private data class ProjectedAtom(
    val atom: Atom3D,
    val screenX: Float,
    val screenY: Float,
    val depthZ: Float,
    val visualRadius: Float
)

@Composable
private fun MoleculeCanvas(
    molecule: MoleculeData,
    rotationX: Float,
    rotationY: Float,
    zoomScale: Float,
    isSpaceFilling: Boolean,
    onAtomClicked: (Atom3D) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val radX = Math.toRadians(rotationX.toDouble()).toFloat()
        val radY = Math.toRadians(rotationY.toDouble()).toFloat()
        val cosX = cos(radX)
        val sinX = sin(radX)
        val cosY = cos(radY)
        val sinY = sin(radY)

        val baseScale = molecule.scaleFactor * zoomScale

        // Project all atoms from 3D space to 2D screen with depth Z
        val projectedAtoms = molecule.atoms.map { atom ->
            // Rotate around Y-axis
            val x1 = atom.x * cosY + atom.z * sinY
            val y1 = atom.y
            val z1 = -atom.x * sinY + atom.z * cosY

            // Rotate around X-axis
            val x2 = x1
            val y2 = y1 * cosX - z1 * sinX
            val z2 = y1 * sinX + z1 * cosX

            // Perspective factor
            val perspective = (z2 + 5f) / 5f
            val screenX = centerX + x2 * baseScale
            val screenY = centerY - y2 * baseScale

            val baseRadius = if (isSpaceFilling) {
                (28f * atom.radiusRatio) * zoomScale
            } else {
                (14f * atom.radiusRatio) * zoomScale
            }

            ProjectedAtom(
                atom = atom,
                screenX = screenX,
                screenY = screenY,
                depthZ = z2,
                visualRadius = baseRadius.coerceAtLeast(6f)
            )
        }.sortedBy { it.depthZ } // Depth-sort (Painter's algorithm): render back to front

        // Draw Bonds
        molecule.bonds.forEach { bond ->
            val p1 = projectedAtoms.firstOrNull { it.atom.id == bond.atom1Index }
            val p2 = projectedAtoms.firstOrNull { it.atom.id == bond.atom2Index }
            if (p1 != null && p2 != null) {
                drawBond3D(p1, p2, bond.order, isSpaceFilling)
            }
        }

        // Draw Atoms with 3D spherical shading and specular highlight
        projectedAtoms.forEach { projected ->
            drawAtom3D(projected, isSpaceFilling)
        }
    }
}

private fun DrawScope.drawBond3D(
    p1: ProjectedAtom,
    p2: ProjectedAtom,
    bondOrder: Int,
    isSpaceFilling: Boolean
) {
    if (isSpaceFilling) return // Space filling bonds are obscured by overlapping spheres

    val p1Offset = Offset(p1.screenX, p1.screenY)
    val p2Offset = Offset(p2.screenX, p2.screenY)
    val dir = p2Offset - p1Offset
    val length = dir.getDistance()
    if (length <= 0) return

    val normal = Offset(-dir.y / length, dir.x / length)

    when (bondOrder) {
        1 -> { // Single bond
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(p1.atom.color.copy(alpha = 0.85f), p2.atom.color.copy(alpha = 0.85f)),
                    start = p1Offset,
                    end = p2Offset
                ),
                start = p1Offset,
                end = p2Offset,
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }
        2 -> { // Double bond
            val offsetDist = 5f
            val o1 = normal * offsetDist
            val o2 = normal * -offsetDist
            drawLine(
                color = Color.LightGray.copy(alpha = 0.9f),
                start = p1Offset + o1,
                end = p2Offset + o1,
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.LightGray.copy(alpha = 0.9f),
                start = p1Offset + o2,
                end = p2Offset + o2,
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        3 -> { // Triple bond
            val offsetDist = 6f
            val o1 = normal * offsetDist
            val o2 = normal * -offsetDist
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = p1Offset + o1,
                end = p2Offset + o1,
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.LightGray.copy(alpha = 0.9f),
                start = p1Offset,
                end = p2Offset,
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = p1Offset + o2,
                end = p2Offset + o2,
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
        }
        4 -> { // Aromatic resonance bond (solid + dashed parallel)
            val offsetDist = 4.5f
            val o1 = normal * offsetDist
            val o2 = normal * -offsetDist
            drawLine(
                color = Color.Cyan.copy(alpha = 0.9f),
                start = p1Offset + o1,
                end = p2Offset + o1,
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            // Inner resonance delocalized line
            drawLine(
                color = Color(0xFF67E8F9).copy(alpha = 0.6f),
                start = p1Offset + o2,
                end = p2Offset + o2,
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawAtom3D(projected: ProjectedAtom, isSpaceFilling: Boolean) {
    val center = Offset(projected.screenX, projected.screenY)
    val r = projected.visualRadius

    // 3D Spherical Radial Gradient (simulates sunlight illumination from top-left)
    val lightOffset = Offset(center.x - r * 0.35f, center.y - r * 0.35f)
    val baseColor = projected.atom.color
    val highlightColor = Color.White.copy(alpha = 0.75f)
    val shadowColor = Color.Black.copy(alpha = 0.65f)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(highlightColor, baseColor, shadowColor),
            center = lightOffset,
            radius = r * 1.25f
        ),
        radius = r,
        center = center
    )

    // Outer subtle rim light
    drawCircle(
        color = Color.White.copy(alpha = 0.25f),
        radius = r,
        center = center,
        style = Stroke(width = 1.2f)
    )
}

// ----------------------------------------------------
// Atomic Orbitals Viewer Canvas (s and px, py, pz)
// ----------------------------------------------------

@Composable
private fun OrbitalsCanvas(
    rotationX: Float,
    rotationY: Float,
    zoomScale: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        val radX = Math.toRadians(rotationX.toDouble()).toFloat()
        val radY = Math.toRadians(rotationY.toDouble()).toFloat()
        val cosX = cos(radX)
        val sinX = sin(radX)
        val cosY = cos(radY)
        val sinY = sin(radY)

        val orbitalScale = 70f * zoomScale

        // Draw 3D coordinate Cartesian Axes (X, Y, Z)
        fun project(x: Float, y: Float, z: Float): Offset {
            val x1 = x * cosY + z * sinY
            val y1 = y
            val z1 = -x * sinY + z * cosY
            val x2 = x1
            val y2 = y1 * cosX - z1 * sinX
            return Offset(cx + x2 * orbitalScale, cy - y2 * orbitalScale)
        }

        val origin = project(0f, 0f, 0f)
        val xAxis = project(2.2f, 0f, 0f)
        val yAxis = project(0f, 2.2f, 0f)
        val zAxis = project(0f, 0f, 2.2f)

        // Axes lines
        drawLine(Color.Red.copy(alpha = 0.6f), origin, xAxis, strokeWidth = 2.5f)
        drawLine(Color.Green.copy(alpha = 0.6f), origin, yAxis, strokeWidth = 2.5f)
        drawLine(Color.Blue.copy(alpha = 0.6f), origin, zAxis, strokeWidth = 2.5f)

        // Draw s-orbital: translucent spherical cloud centered at origin
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.45f), Color(0xFF0284C7).copy(alpha = 0.15f), Color.Transparent),
                center = origin,
                radius = orbitalScale * 0.9f
            ),
            radius = orbitalScale * 0.9f,
            center = origin
        )
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = 0.5f),
            radius = orbitalScale * 0.9f,
            center = origin,
            style = Stroke(width = 1.5f)
        )

        // Draw px orbital dumbbell lobes (along X axis: + lobe and - lobe)
        val pxLobePos = project(1.1f, 0f, 0f)
        val pxLobeNeg = project(-1.1f, 0f, 0f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFF43F5E).copy(alpha = 0.7f), Color.Transparent),
                center = pxLobePos,
                radius = orbitalScale * 0.65f
            ),
            radius = orbitalScale * 0.65f,
            center = pxLobePos
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFB7185).copy(alpha = 0.7f), Color.Transparent),
                center = pxLobeNeg,
                radius = orbitalScale * 0.65f
            ),
            radius = orbitalScale * 0.65f,
            center = pxLobeNeg
        )

        // Draw py orbital dumbbell lobes (along Y axis)
        val pyLobePos = project(0f, 1.1f, 0f)
        val pyLobeNeg = project(0f, -1.1f, 0f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF10B981).copy(alpha = 0.6f), Color.Transparent),
                center = pyLobePos,
                radius = orbitalScale * 0.6f
            ),
            radius = orbitalScale * 0.6f,
            center = pyLobePos
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF34D399).copy(alpha = 0.6f), Color.Transparent),
                center = pyLobeNeg,
                radius = orbitalScale * 0.6f
            ),
            radius = orbitalScale * 0.6f,
            center = pyLobeNeg
        )

        // Nucleus
        drawCircle(Color.White, radius = 5f, center = origin)
    }
}

// ----------------------------------------------------
// Daniell Cell Interactive Schematic
// ----------------------------------------------------

@Composable
private fun DaniellCellCanvas(zoomScale: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        val scale = zoomScale.coerceIn(0.7f, 1.5f)

        val beakerWidth = 110f * scale
        val beakerHeight = 140f * scale
        val beakerGap = 70f * scale

        val leftBeakerX = cx - beakerGap - beakerWidth
        val rightBeakerX = cx + beakerGap
        val beakersY = cy - 20f

        // 1. Left Beaker (ZnSO4 solution - Zinc half cell)
        // Solution
        drawRect(
            color = Color(0xFF60A5FA).copy(alpha = 0.25f),
            topLeft = Offset(leftBeakerX + 5f, beakersY + 40f),
            size = androidx.compose.ui.geometry.Size(beakerWidth - 10f, beakerHeight - 40f)
        )
        // Glass wall
        drawRect(
            color = Color.White.copy(alpha = 0.6f),
            topLeft = Offset(leftBeakerX, beakersY),
            size = androidx.compose.ui.geometry.Size(beakerWidth, beakerHeight),
            style = Stroke(width = 3f)
        )
        // Zinc Electrode (Anode - Grey)
        drawRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(leftBeakerX + beakerWidth / 2f - 12f, beakersY - 30f),
            size = androidx.compose.ui.geometry.Size(24f, beakerHeight)
        )

        // 2. Right Beaker (CuSO4 solution - Copper half cell)
        // Solution (Deep blue)
        drawRect(
            color = Color(0xFF2563EB).copy(alpha = 0.5f),
            topLeft = Offset(rightBeakerX + 5f, beakersY + 40f),
            size = androidx.compose.ui.geometry.Size(beakerWidth - 10f, beakerHeight - 40f)
        )
        // Glass wall
        drawRect(
            color = Color.White.copy(alpha = 0.6f),
            topLeft = Offset(rightBeakerX, beakersY),
            size = androidx.compose.ui.geometry.Size(beakerWidth, beakerHeight),
            style = Stroke(width = 3f)
        )
        // Copper Electrode (Cathode - Reddish brown)
        drawRect(
            color = Color(0xFFD97706),
            topLeft = Offset(rightBeakerX + beakerWidth / 2f - 12f, beakersY - 30f),
            size = androidx.compose.ui.geometry.Size(24f, beakerHeight)
        )

        // 3. Salt Bridge (U-tube connecting the two beakers)
        val bridgeY = beakersY + 20f
        val bridgeLeft = leftBeakerX + beakerWidth - 25f
        val bridgeRight = rightBeakerX + 25f
        val bridgeTop = beakersY - 45f

        val bridgePath = Path().apply {
            moveTo(bridgeLeft, beakersY + 80f)
            lineTo(bridgeLeft, bridgeTop)
            lineTo(bridgeRight, bridgeTop)
            lineTo(bridgeRight, beakersY + 80f)
        }
        drawPath(bridgePath, color = Color(0xFFFDE047).copy(alpha = 0.75f), style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // 4. External Circuit Wire & Voltmeter
        val wireY = beakersY - 70f
        val wireLeft = leftBeakerX + beakerWidth / 2f
        val wireRight = rightBeakerX + beakerWidth / 2f

        drawLine(Color.Yellow, Offset(wireLeft, beakersY - 30f), Offset(wireLeft, wireY), strokeWidth = 3f)
        drawLine(Color.Yellow, Offset(wireLeft, wireY), Offset(cx - 24f, wireY), strokeWidth = 3f)
        drawLine(Color.Yellow, Offset(cx + 24f, wireY), Offset(wireRight, wireY), strokeWidth = 3f)
        drawLine(Color.Yellow, Offset(wireRight, wireY), Offset(wireRight, beakersY - 30f), strokeWidth = 3f)

        // Voltmeter meter body
        drawCircle(Color(0xFF0F172A), radius = 24f, center = Offset(cx, wireY))
        drawCircle(Color.Yellow, radius = 24f, center = Offset(cx, wireY), style = Stroke(width = 2.5f))
        // Pointer at +1.10 V
        drawLine(Color.Red, Offset(cx, wireY), Offset(cx + 14f, wireY - 14f), strokeWidth = 2.5f)

        // Electron flow arrow
        val arrowStartX = leftBeakerX + beakerWidth / 2f + 30f
        val arrowEndX = arrowStartX + 40f
        drawLine(Color.Cyan, Offset(arrowStartX, wireY - 12f), Offset(arrowEndX, wireY - 12f), strokeWidth = 2.5f)
        drawLine(Color.Cyan, Offset(arrowEndX, wireY - 12f), Offset(arrowEndX - 8f, wireY - 18f), strokeWidth = 2.5f)
        drawLine(Color.Cyan, Offset(arrowEndX, wireY - 12f), Offset(arrowEndX - 8f, wireY - 6f), strokeWidth = 2.5f)
    }
}
