package com.durelljardim.kitabu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.durelljardim.kitabu.R

@Composable
fun BookCover(title: String, modifier: Modifier = Modifier) {
    val cover = coverFor(title)
    if (cover == null) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        )
    } else {
        Image(
            painter = painterResource(cover),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

private fun coverFor(title: String): Int? = when (title) {
    "Introduction to Information Systems" -> R.drawable.cover_intro_information_systems
    "Python Crash Course" -> R.drawable.cover_python_crash_course
    "Head First Java" -> R.drawable.cover_head_first_java
    "Database System Concepts" -> R.drawable.cover_database_system_concepts
    "HTML and CSS: Design and Build Websites" -> R.drawable.cover_html_and_css
    "JavaScript and jQuery" -> R.drawable.cover_javascript_and_jquery
    "Learning PHP, MySQL and JavaScript" -> R.drawable.cover_learning_php_mysql_javascript
    "SVG Animations" -> R.drawable.cover_svg_animations
    "WordPress: The Missing Manual" -> R.drawable.cover_wordpress_missing_manual
    "Head First Android Development" -> R.drawable.cover_head_first_android
    "Computer Security: Principles and Practice" -> R.drawable.cover_computer_security
    "Statistics for Business and Economics" -> R.drawable.cover_statistics_business_economics
    "Research Methods for Business Students" -> R.drawable.cover_research_methods_business
    else -> null
}
