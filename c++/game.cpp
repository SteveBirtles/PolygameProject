#define OLC_PGE_APPLICATION
#include "olcPixelGameEngine.h"

class Game : public olc::PixelGameEngine {
 public:
  const static int WINDOW_WIDTH = 1920;
  const static int WINDOW_HEIGHT = 1080;

 private:
  float timer = 0;
  int frames = 0;
  int fps;

  struct Invader {
    float x;
    float y;
    float dx;
    float dy;
    Invader(float givenX, float givenY, float givenDx, float givenDy) {
      x = givenX;
      y = givenY;
      dx = givenDx;
      dy = givenDy;
    }
    void update(float frameLength) {
      x += dx * frameLength;
      y += dy * frameLength;
      if (x < -100) x += WINDOW_WIDTH + 200;
      if (y < -100) y += WINDOW_HEIGHT + 200;
      if (x > 100 + WINDOW_WIDTH) x -= WINDOW_WIDTH + 200;
      if (y > 100 + WINDOW_HEIGHT) y -= WINDOW_HEIGHT + 200;
    }
  };

  olc::Sprite* invaderSprite;
  olc::Decal* invaderDecal;
  std::vector<Invader> invaders;

  void inputs() {
    /*
      Game controls goes here
    */

    if (GetKey(olc::Key::SPACE).bHeld) {
      std::cout << "Space!" << std::endl;
    }
    if (GetMouse(0).bHeld) {
      std::cout << "Click!" << std::endl;
    }
  }

  void processes() {
    /*
      Game logic goes here
    */
    for (auto& invader : invaders) {
      invader.update(GetElapsedTime());
    }
  }

  void outputs() {
    SetPixelMode(olc::Pixel::NORMAL);

    /*
      Game graphics drawn here
    */

    for (auto& invader : invaders) {
      DrawDecal({invader.x - invaderSprite->width / 2,
                 invader.y - invaderSprite->height / 2},
                invaderDecal);
    }

    if (fps > 0) {
      auto fpsPosition = olc::vi2d(20, 20);
      DrawStringDecal(fpsPosition, "C++ (olcPixelGameEngine)   " +
                                       std::to_string(fps) + " FPS");
    }
  }

 public:
  Game() { sAppName = "Polygame Project - C++"; }

  bool OnUserCreate() override {
    /*
      Load resources here
    */
    std::string path = "./resources/";
    invaderSprite = new olc::Sprite(path + "sprite.png");
    invaderDecal = new olc::Decal(invaderSprite);

    for (auto i = 0; i <= 100; i++) {
      invaders.push_back(Invader(rand() % WINDOW_WIDTH, rand() % WINDOW_WIDTH,
                                 rand() % 100 - 50, rand() % 100 - 50));
    }

    return true;
  }

  bool OnUserUpdate(float fElapsedTime) override {
    timer += fElapsedTime;
    frames++;
    if (timer > 1.0) {
      fps = frames;
      frames = 0;
      timer -= 1;
    }

    inputs();
    processes();
    outputs();

    if (GetKey(olc::Key::ESCAPE).bPressed) {
      return false;
    } else {
      return true;
    }
  }

  bool OnUserDestroy() override {
    std::cout << "Closing game" << std::endl;
    return true;
  }
};

int main() {
  Game game;
  if (game.Construct(game.WINDOW_WIDTH, game.WINDOW_HEIGHT, 1, 1, true))
    game.Start();
  return 0;
}
