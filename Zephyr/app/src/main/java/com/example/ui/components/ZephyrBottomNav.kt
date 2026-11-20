package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZephyrCardBorder
import com.example.ui.theme.ZephyrCharcoal
import com.example.ui.theme.ZephyrTaupe

enum class NavigationDestination {
    HOME,
    SHOP,
    TRANSACTIONS,
    INVESTMENTS,
    INSIGHTS,
    PROFILE
}

@Composable
fun ZephyrBottomNav(
    currentDestination: NavigationDestination,
    onNavigate: (NavigationDestination) -> Unit,
    onEarnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFAF8F5))
            .navigationBarsPadding()
    ) {
        // Top hairline divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(ZephyrCardBorder)
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 1. Home
            NavTabItem(
                label = "Home",
                icon = Icons.Outlined.Home,
                isSelected = currentDestination == NavigationDestination.HOME,
                testTag = "nav_home",
                onClick = { onNavigate(NavigationDestination.HOME) }
            )

            // 2. Buy Assets
            NavTabItem(
                label = "Buy Assets",
                icon = Icons.Outlined.ShoppingBag,
                isSelected = currentDestination == NavigationDestination.SHOP,
                testTag = "nav_shop",
                onClick = { onNavigate(NavigationDestination.SHOP) }
            )

            // 3. Central Action Button: "Earn" with 3 vertical bars
            CenterEarnButton(
                onClick = onEarnClick,
                testTag = "nav_center_earn"
            )

            // 4. Insights
            NavTabItem(
                label = "Insights",
                icon = Icons.Outlined.BarChart,
                isSelected = currentDestination == NavigationDestination.INSIGHTS,
                testTag = "nav_insights",
                onClick = { onNavigate(NavigationDestination.INSIGHTS) }
            )

            // 5. Profile
            NavTabItem(
                label = "Profile",
                icon = Icons.Outlined.PersonOutline,
                isSelected = currentDestination == NavigationDestination.PROFILE,
                testTag = "nav_profile",
                onClick = { onNavigate(NavigationDestination.PROFILE) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val tint = if (isSelected) ZephyrCharcoal else ZephyrTaupe

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = tint,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun CenterEarnButton(
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(46.dp)
            .shadow(elevation = 4.dp, shape = CircleShape, spotColor = Color(0x33000000))
            .clip(CircleShape)
            .background(ZephyrCharcoal)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // 3 Vertical Wave Bars matching reference image button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
        }
    }
}
