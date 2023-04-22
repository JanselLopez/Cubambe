package com.jansellopez.cubambe.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.ui.home.HomeScreen
import com.jansellopez.cubambe.ui.routes.Destination
import com.jansellopez.cubambe.ui.player.PlayerScreen
import kotlinx.coroutines.launch

private lateinit var routes:List<Destination>

@Composable
fun CubambeApp(songs:List<Song>){
    routes = listOf(
        Destination(stringResource(id = R.string.home),Icons.Filled.Home,"home_screen"),
        Destination(stringResource(id = R.string.downloads),Icons.Filled.PlayArrow,"player_screen"),
    )
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentStack = currentBackStack?.destination
    val currentScreen = routes.find { screen -> screen.route == currentStack?.route }?:routes[0]

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { AppBar{
            scope.launch {
                scaffoldState.drawerState.open()
            }
        } },
        bottomBar = { BottomMenu(currentScreen){
                route -> navController.navigate(route){launchSingleTop=true}
        } },
        drawerContent = {
            AboutDrawer()
        }
    ) {innerPadding ->
        CubambeHost(songs= songs,navController = navController,modifier = Modifier.padding(innerPadding))
    }
}

@Preview
@Composable
fun AboutDrawer() {
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(id = R.string.app_name), fontFamily = FontFamily(Font(R.font.montserrat_bold)), fontSize = 30.sp, modifier = Modifier.padding(top = 20.dp))
                Image(painter = painterResource(id = R.drawable.ic_logo_red_without_background), contentDescription = "logo", modifier = Modifier.padding(vertical = 10.dp))
                Text(
                    text= stringResource(id = R.string.app_desc),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(text = stringResource(id = R.string.by) + " jansellopez", modifier = Modifier
                        .weight(2f)
                        .padding(5.dp), textAlign = TextAlign.Center)
                    Divider(modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Button(onClick = {
                        val uri = Uri.parse("mailto:21jansel@gmail.com")
                        val i = Intent(Intent.ACTION_SENDTO,uri)
                        ContextCompat.startActivity(context, i, Bundle())
                    }, modifier = Modifier.weight(1f)) {
                        Icon(modifier = Modifier.padding(end = 5.dp),painter = painterResource(id = R.drawable.gmail), contentDescription = "GMAIL", tint = Color.White)
                        Text(text = "Gmail", color =Color.White)
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    Button(onClick = {
                        val uri: Uri = Uri.parse("https://github.com/JanselLopez/Cubambe")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    }, modifier = Modifier.weight(1f)) {
                        Icon(modifier = Modifier.padding(end = 5.dp),painter = painterResource(id = R.drawable.github), contentDescription = "Github", tint = Color.White)
                        Text(text = "Github", color =Color.White)
                    }
                }

            }
        }



    }
}

@Composable
fun CubambeHost(songs: List<Song>,navController: NavHostController, modifier: Modifier = Modifier){
    NavHost(navController = navController, startDestination = routes[0].route , modifier = modifier ){
       composable(routes[0].route){
           HomeScreen(songs)
       }
        composable(routes[1].route){
            PlayerScreen()
        }
    }
}

@Composable
fun BottomMenu(currentScreen:Destination,onBottomItemPress:(route:String)->Unit) {
    BottomNavigation(backgroundColor = Color.Transparent, elevation = 0.dp) {
        routes.forEach { destination ->
            BottomNavigationItem(selected = currentScreen == destination, onClick = { onBottomItemPress(destination.route) },
                icon = { Icon(destination.icon, contentDescription = destination.title) },
                label = { Text(destination.title, fontFamily = FontFamily(Font(R.font.montserrat_bold))) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
            )
        }

    }
}

@Composable
fun AppBar( onNavigationClick: ()-> Unit) {
 TopAppBar(
     title = { Text(stringResource(id = R.string.app_name), fontFamily = FontFamily(listOf(Font(R.font.montserrat_variable)))) } ,
     backgroundColor = MaterialTheme.colors.background,
     actions = {
         IconButton(onClick = onNavigationClick) {
             Icon(Icons.Filled.Menu, contentDescription = "Drawer")
         }
     },
     elevation = 0.dp
 ) 
}
