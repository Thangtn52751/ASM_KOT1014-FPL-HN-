package dev.zero.myapplication.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import dev.zero.myapplication.R
import dev.zero.myapplication.api.models.IntroSlide
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroScreen(navController: NavController) {
    val introSlides = listOf(
        IntroSlide(
            imageRes = R.drawable.intro1,
            title = "Endless Sweet Delights",
            description = "Indulge in a world of delicious desserts that will satisfy your sweet tooth with our wide selection of cakes, cupcakes, and cookies."
        ),
        IntroSlide(
            imageRes = R.drawable.intro2,
            title = "Create Your Dream Cake",
            description = "Our flexible custom cake designs let you personalize every aspect of your cake to make it truly unique."
        ),
        IntroSlide(
            imageRes = R.drawable.intro3,
            title = "Hassle-Free Ordering",
            description = "Enjoy convenience and ease with our online ordering experience as we deliver your favorite treats to your doorstep."
        )
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HorizontalPager(
            count = introSlides.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            IntroSlideContent(introSlides[page])
        }

        // Indicator Dots
        DotsIndicator(
            totalDots = introSlides.size,
            selectedIndex = pagerState.currentPage,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Button
        Button(
            onClick = {
                if (pagerState.currentPage < introSlides.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    navController.navigate("signin")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F61))
        ) {
            Text(
                text = if (pagerState.currentPage == introSlides.size - 1) "Start Shopping" else "Next",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun IntroSlideContent(slide: IntroSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = slide.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = slide.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = slide.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == selectedIndex) 10.dp else 8.dp)
                    .background(
                        if (index == selectedIndex) Color(0xFFFF6F61) else Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    }
}
