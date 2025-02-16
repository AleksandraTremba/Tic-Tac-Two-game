# Tic Tac Two project for Native Mobile Apps Development


## Features
#### Very cool icon
![](assets/icon.PNG)

#### Game UI
- Different screens
<br>

![](assets/empty.png)
![](assets/menu.png)
![](assets/settings.png)
![](assets/stats.png)

- Supports landscape rotation
<br>

![](assets/full.png)
![](assets/landscape.png)

- Gameplay screen (main activity) supports rotation AND tablet screen size. The rest are simple and responsive!
<br>

![](assets/layouts.png)

- Supports dark/light mode (system based)
<br>

![](assets/light.png)
![](assets/light_landscape.png)
![](assets/themes.png)

- Has sounds added (different for X and O players!)

#### Database functionality
- SQLite db
- Saves game state when game is closed, and loads it when it opens again
- Statistics are saved to see how it changed over time
- Single table with just single state for every saved game
<br>

![](assets/db.png)


#### Gameplay
- Users play tic tac toe on 3x3 grid
- When 2 tokens from each player is placed, the grid can be moved across bigger, 5x5 grid. Moving the grid counts as a turn
- Moving grid is using drag-and-drop buttons to place
- Human vs Human and Human vs Bot supported
- Bot functionality! (30% chance that he will move the grid)
- All wins are saved for statistics

#### Hope you enjoy! :)

