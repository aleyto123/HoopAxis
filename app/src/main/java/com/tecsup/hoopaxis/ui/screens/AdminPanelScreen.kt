package com.tecsup.hoopaxis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.data.model.QuizQuestion
import com.tecsup.hoopaxis.ui.theme.AppColors
import com.tecsup.hoopaxis.viewmodel.AdminViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: AdminViewModel = viewModel(factory = AdminViewModel.provideFactory(repository))

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Reglas", "Capítulos", "Artículos", "Quiz")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.pushToCloud() }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Subir a la nube", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.syncRemote() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sincronizar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        },
        containerColor = AppColors.Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AppColors.Background,
                contentColor = AppColors.Purple
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }

            when (selectedTab) {
                0 -> RulesManager(viewModel)
                1 -> ChaptersManager(viewModel)
                2 -> ArticlesManager(viewModel)
                3 -> QuizManager(viewModel)
            }
        }
    }
}

@Composable
fun RulesManager(viewModel: AdminViewModel) {
    val rules by viewModel.rules.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<Rule?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { editingRule = null; showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
        ) {
            Icon(Icons.Default.Add, null)
            Text("AGREGAR REGLA")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rules) { rule ->
                AdminItemCard(
                    title = "Regla ${rule.number}: ${rule.title}",
                    onEdit = { editingRule = rule; showDialog = true },
                    onDelete = { viewModel.deleteRule(rule) }
                )
            }
        }
    }

    if (showDialog) {
        RuleDialog(
            rule = editingRule,
            onDismiss = { showDialog = false },
            onConfirm = { 
                if (editingRule == null) viewModel.addRule(it) else viewModel.updateRule(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun RuleDialog(rule: Rule?, onDismiss: () -> Unit, onConfirm: (Rule) -> Unit) {
    var title by remember { mutableStateOf(rule?.title ?: "") }
    var number by remember { mutableStateOf(rule?.number?.toString() ?: "") }
    var subtitle by remember { mutableStateOf(rule?.subtitle ?: "") }
    var emoji by remember { mutableStateOf(rule?.emoji ?: "🏀") }
    var color by remember { mutableStateOf(rule?.color ?: "#C96BFF") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (rule == null) "Nueva Regla" else "Editar Regla") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = number, onValueChange = { number = it }, label = { Text("Número") })
                TextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                TextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtítulo") })
                TextField(value = emoji, onValueChange = { emoji = it }, label = { Text("Emoji") })
                TextField(value = color, onValueChange = { color = it }, label = { Text("Color Hex") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(Rule(
                    id = rule?.id ?: UUID.randomUUID().toString(),
                    number = number.toIntOrNull() ?: 0,
                    title = title,
                    subtitle = subtitle,
                    emoji = emoji,
                    color = color,
                    glow = color + "44",
                    chaptersCount = rule?.chaptersCount ?: 0,
                    progress = rule?.progress ?: 0f
                ))
            }) { Text("GUARDAR") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR") } }
    )
}

@Composable
fun ChaptersManager(viewModel: AdminViewModel) {
    val chapters by viewModel.chapters.collectAsState()
    val rules by viewModel.rules.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingChapter by remember { mutableStateOf<Chapter?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { editingChapter = null; showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Pink)
        ) {
            Icon(Icons.Default.Add, null)
            Text("AGREGAR CAPÍTULO")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chapters) { chapter ->
                AdminItemCard(
                    title = "Cap. ${chapter.number}: ${chapter.title} (R: ${chapter.ruleId})",
                    onEdit = { editingChapter = chapter; showDialog = true },
                    onDelete = { viewModel.deleteChapter(chapter) }
                )
            }
        }
    }

    if (showDialog) {
        ChapterDialog(
            chapter = editingChapter,
            rules = rules,
            onDismiss = { showDialog = false },
            onConfirm = { 
                if (editingChapter == null) viewModel.addChapter(it) else viewModel.updateChapter(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun ChapterDialog(chapter: Chapter?, rules: List<Rule>, onDismiss: () -> Unit, onConfirm: (Chapter) -> Unit) {
    var title by remember { mutableStateOf(chapter?.title ?: "") }
    var number by remember { mutableStateOf(chapter?.number?.toString() ?: "") }
    var ruleId by remember { mutableStateOf(chapter?.ruleId ?: rules.firstOrNull()?.id ?: "") }
    var emoji by remember { mutableStateOf(chapter?.emoji ?: "📌") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (chapter == null) "Nuevo Capítulo" else "Editar Capítulo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = ruleId, onValueChange = { ruleId = it }, label = { Text("ID de Regla") })
                TextField(value = number, onValueChange = { number = it }, label = { Text("Número") })
                TextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                TextField(value = emoji, onValueChange = { emoji = it }, label = { Text("Emoji") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(Chapter(
                    id = chapter?.id ?: UUID.randomUUID().toString(),
                    ruleId = ruleId,
                    number = number.toIntOrNull() ?: 0,
                    title = title,
                    emoji = emoji,
                    articlesCount = chapter?.articlesCount ?: 0,
                    progress = chapter?.progress ?: 0f
                ))
            }) { Text("GUARDAR") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR") } }
    )
}

@Composable
fun ArticlesManager(viewModel: AdminViewModel) {
    val articles by viewModel.articles.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingArticle by remember { mutableStateOf<Article?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { editingArticle = null; showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Blue)
        ) {
            Icon(Icons.Default.Add, null)
            Text("AGREGAR ARTÍCULO")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(articles) { article ->
                AdminItemCard(
                    title = "${article.articleNumber}: ${article.title} (Cap: ${article.chapterId})",
                    onEdit = { editingArticle = article; showDialog = true },
                    onDelete = { viewModel.deleteArticle(article) }
                )
            }
        }
    }

    if (showDialog) {
        ArticleDialog(
            article = editingArticle,
            chapters = chapters,
            onDismiss = { showDialog = false },
            onConfirm = { 
                if (editingArticle == null) viewModel.addArticle(it) else viewModel.updateArticle(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun ArticleDialog(article: Article?, chapters: List<Chapter>, onDismiss: () -> Unit, onConfirm: (Article) -> Unit) {
    var title by remember { mutableStateOf(article?.title ?: "") }
    var number by remember { mutableStateOf(article?.articleNumber ?: "") }
    var chapterId by remember { mutableStateOf(article?.chapterId ?: chapters.firstOrNull()?.id ?: "") }
    var paraphrase by remember { mutableStateOf(article?.paraphrase ?: "") }
    var officialText by remember { mutableStateOf(article?.officialText ?: "") }
    var keyPointsStr by remember { mutableStateOf(article?.keyPoints?.joinToString("\n") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (article == null) "Nuevo Artículo" else "Editar Artículo") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = chapterId, onValueChange = { chapterId = it }, label = { Text("ID Capítulo") })
                TextField(value = number, onValueChange = { number = it }, label = { Text("Número (Ej: Art. 1)") })
                TextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                TextField(value = paraphrase, onValueChange = { paraphrase = it }, label = { Text("Parafraseo") }, modifier = Modifier.height(100.dp))
                TextField(value = officialText, onValueChange = { officialText = it }, label = { Text("Texto Oficial") }, modifier = Modifier.height(100.dp))
                TextField(value = keyPointsStr, onValueChange = { keyPointsStr = it }, label = { Text("Puntos Clave (uno por línea)") }, modifier = Modifier.height(100.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(Article(
                    id = article?.id ?: UUID.randomUUID().toString(),
                    chapterId = chapterId,
                    title = title,
                    articleNumber = number,
                    paraphrase = paraphrase,
                    officialText = officialText,
                    keyPoints = keyPointsStr.split("\n").filter { it.isNotBlank() },
                    isCompleted = article?.isCompleted ?: false
                ))
            }) { Text("GUARDAR") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR") } }
    )
}

@Composable
fun QuizManager(viewModel: AdminViewModel) {
    val questions by viewModel.quizQuestions.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingQuestion by remember { mutableStateOf<QuizQuestion?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { editingQuestion = null; showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Blue)
        ) {
            Icon(Icons.Default.Add, null)
            Text("AGREGAR PREGUNTA QUIZ")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(questions) { q ->
                AdminItemCard(
                    title = q.question,
                    onEdit = { editingQuestion = q; showDialog = true },
                    onDelete = { viewModel.deleteQuizQuestion(q) }
                )
            }
        }
    }
    
    if (showDialog) {
        QuizQuestionDialog(
            question = editingQuestion,
            onDismiss = { showDialog = false },
            onConfirm = {
                if (editingQuestion == null) viewModel.addQuizQuestion(it) else viewModel.updateQuizQuestion(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun QuizQuestionDialog(question: QuizQuestion?, onDismiss: () -> Unit, onConfirm: (QuizQuestion) -> Unit) {
    var text by remember { mutableStateOf(question?.question ?: "") }
    var category by remember { mutableStateOf(question?.category ?: "") }
    var explanation by remember { mutableStateOf(question?.explanation ?: "") }
    var optionsStr by remember { mutableStateOf(question?.options?.joinToString(",") ?: "") }
    var correctIndex by remember { mutableStateOf(question?.correctIndex?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (question == null) "Nueva Pregunta" else "Editar Pregunta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = text, onValueChange = { text = it }, label = { Text("Pregunta") })
                TextField(value = category, onValueChange = { category = it }, label = { Text("Categoría") })
                TextField(value = optionsStr, onValueChange = { optionsStr = it }, label = { Text("Opciones (separadas por coma)") })
                TextField(value = correctIndex, onValueChange = { correctIndex = it }, label = { Text("Índice Correcto (0-3)") })
                TextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Explicación") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(QuizQuestion(
                    id = question?.id ?: UUID.randomUUID().toString(),
                    question = text,
                    category = category,
                    options = optionsStr.split(","),
                    correctIndex = correctIndex.toIntOrNull() ?: 0,
                    explanation = explanation
                ))
            }) { Text("GUARDAR") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR") } }
    )
}

@Composable
fun AdminItemCard(title: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White, modifier = Modifier.weight(1f), fontSize = 14.sp, maxLines = 1)
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = AppColors.Purple) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
        }
    }
}
