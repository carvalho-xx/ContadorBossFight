# ⚔️ Contador de Bosses (`ContadorBossFight`)

Aplicativo Android nativo para acompanhar suas batalhas contra chefes em jogos **Soulsborne**, **Action-RPG** e **Hack and Slash**. Registre cada tentativa, contabilize suas mortes, cronometre o tempo exato de combate (incluindo horas e transições de fase) e marque seus adversários como **Inimigo Abatido**.

> 🤖 **Desenvolvimento Assistido por IA**: Este projeto foi idealizado, arquitetado e iterado em conjunto com Inteligência Artificial (*AI Pair Programming*), combinando engenharia Android moderna (Kotlin + Jetpack Compose + Room) com direção de arte inspirada em HUDs de RPGs Dark Fantasy.

---

## ✨ Funcionalidades Principais

### 🏰 Gestão de Reinos (Jogos) e Temas Dinâmicos
- Cadastre múltiplos jogos e acompanhe o progresso geral de cada reino (**Bosses Abatidos** e **Total de Mortes**).
- **Motor de Temas Atmosféricos (`HudAtmosphericBackground`)**: O aplicativo adapta automaticamente sua paleta de cores, iluminação de horizonte, partículas e citações épicas de acordo com o jogo selecionado:
  - **Elden Ring** — *As Terras Intermédias* (Dourado Áureo & Névoa de Stormveil)
  - **Dark Souls** — *Reino de Lordran* (Chama Primordial & Cinzas)
  - **Dark Souls II** — *Reino de Drangleic* (Brasa de Majula & Bronze)
  - **Dark Souls III** — *Reino de Lothric* (Fogo dos Lordes & Cinzas)
  - **Phantom Blade** — *O Mundo Fantasma* (Vermelho Sangue & Sombra Oriental)
  - **Bloodborne** — *Pesadelo de Yharnam* (Sangue Pálido & Noite Gótica)
  - **Sekiro** — *Terras de Ashina* (Vermelhão Shinobi & Névoa)
  - **Jogos Personalizados** — Suporte a qualquer outro jogo com tema *Salão dos Desafiantes* (Aço Escuro & Azul Astral).

### 💀 Contador de Mortes ("O Preço do Desafio")
- Incremento e decremento rápido com botões dedicados no HUD (`+` / `−`).
- **Inserção direta de valor**: Toque no número de mortes (`✎ DEFINIR VALOR`) para digitar diretamente a quantidade de mortes acumuladas — ideal para importar dados de chefes que você já enfrentou antes de usar o app.
- Exibição automática da tentativa atual em algarismos romanos (`TENTATIVA I`, `TENTATIVA VIII`, etc.).

### ⏱️ Cronômetro de Combate com Horas e Fases
- Formato completo com horas, minutos, segundos e centésimos (`HH:MM:SS.cc`), pensado para lutas longas que ultrapassam mais de uma hora de tentativas.
- **Persistência baseada em Timestamp (`epochMillis`)**: O cronômetro continua preciso mesmo se o aplicativo for fechado ou se a tela do celular apagar durante a luta.
- **Transições de Fase (`Marcar`)**: Registre tempos parciais (`FASE I`, `FASE II`) durante a luta para analisar quanto tempo levou em cada fase do chefe.
- **Edição Manual de Tempo**: Toque no visor do cronômetro (`✎ DEFINIR TEMPO MANUALMENTE`) ou preencha no cadastro do boss para registrar o tempo de chefes que você já derrotou anteriormente.

### 🏆 Sistema de Vitória ("Inimigo Abatido" & NG+)
- Botão **`⚔ MATAR BOSS (INIMIGO ABATIDO)`** na tela de combate:
  - Pausa e salva automaticamente o tempo final da luta.
  - Exibe o banner comemorativo **`★ INIMIGO ABATIDO ★`** com o resumo da conquista.
  - Destaca o card do chefe na lista do jogo com selo e borda iluminada (`✓ Abatido`).
  - Congela os botões de incremento acidental para preservar seu histórico, permitindo reabrir a luta a qualquer momento com **`↺ REABRIR COMBATE (NG+)`**.

---

## 🛠️ Tecnologias e Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)** com separação clara entre camada de dados e interface reativa:

- **Linguagem**: [Kotlin](https://kotlinlang.org/)
- **Interface (UI)**: [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material 3 + `Canvas` customizado para renderização procedural de cenários, ícones e divisores estilo HUD
- **Navegação**: Jetpack Navigation Compose (`BossFightNavHost`)
- **Gerenciamento de Estado**: Kotlin Coroutines & `StateFlow` (`collectAsStateWithLifecycle`)
- **Persistência Local**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite offline com suporte a `TypeConverters` e `Migration` automática de esquema)

### Estrutura de Pacotes

```text
io.github.carvalhoxx
├── data
│   ├── local
│   │   ├── dao/BossFightDao.kt          # Queries reativas com Flow e operações CRUD
│   │   ├── entity/GameEntity.kt         # Entidade de Jogos (Reinos)
│   │   ├── entity/BossEntity.kt         # Entidade de Bosses (mortes, tempo, fases, status abatido)
│   │   ├── AppDatabase.kt               # Configuração do banco Room + Migrations
│   │   └── Converters.kt                # Conversão de listas de fases (laps)
│   └── repository/BossFightRepository.kt # Regras de negócio de cronômetro, mortes e vitórias
├── ui
│   ├── components/HudComponents.kt      # Componentes visuais do HUD (painéis glassmorphic, botões metálicos, ícones)
│   ├── navigation/BossFightNavHost.kt   # Rotas de navegação entre telas
│   ├── screen
│   │   ├── GameListScreen.kt            # Tela principal de seleção e cadastro de jogos
│   │   ├── BossListScreen.kt            # Lista de adversários do jogo selecionado
│   │   └── BossDetailScreen.kt          # HUD imersivo de combate, contador e cronômetro
│   ├── theme                            # Paletas dinâmicas (Color.kt, Theme.kt, Type.kt)
│   ├── util/TimeFormatUtils.kt          # Formatação HH:MM:SS.cc e algarismos romanos
│   └── viewmodel                        # ViewModels reativos (GameList, BossList, BossDetail)
└── MainActivity.kt                      # Ponto de entrada Single-Activity com Edge-to-Edge
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Android Studio** (Ladybug ou superior recomendado)
- **JDK 17+**
- Dispositivo físico ou emulador Android com **API 24 (Android 7.0)** ou superior

### Passo a passo
1. Clone este repositório:
   ```bash
   git clone git@github.com:carvalho-xx/ContadorBossFight.git
   cd ContadorBossFight
   ```
2. Abra o projeto no **Android Studio** ou compile diretamente via terminal:
   ```bash
   ./gradlew assembleDebug
   ```
3. Para instalar no dispositivo conectado via ADB:
   ```bash
   ./gradlew installDebug
   ```

---

## 🎮 Como Usar

1. **Escolha seu Reino**: Na tela inicial, toque em **`+ ADICIONAR JOGO`** e escolha um preset da lista (ex: *Elden Ring*, *Dark Souls III*, *Phantom Blade*) ou selecione **`Cadastrar jogo novo...`** para digitar qualquer título.
2. **Cadastre seus Adversários**: Entre no jogo e toque em **`+ ADICIONAR BOSS`**. Se for um boss que você já derrotou no passado, você já pode informar as mortes, o tempo gasto e marcar a opção **"Boss já derrotado (Inimigo Abatido)"**.
3. **Entre em Combate**: Toque em **`⚔ Lutar`** para abrir o HUD do boss. Use **`Iniciar`** para rodar o cronômetro, **`+`** para cada morte (ou toque no número para digitar um valor) e **`⚔ MATAR BOSS (INIMIGO ABATIDO)`** quando conquistar a vitória!
