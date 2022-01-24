"use strict";
p5.disableFriendlyErrors = true;

const WINDOW_WIDTH = 1920
const WINDOW_HEIGHT = 1080

let invaderSprite;
let invaders = [];
let fps = 0;
let frameCounter = 0;
let fpsTimer = 0;

function preload() {
  invaderSprite = loadImage("resources/sprite.png");
}

function setup() {
  createCanvas(WINDOW_WIDTH, WINDOW_HEIGHT, P2D);
  frameRate(60);

  for (let i = 0; i < 100; i++) {
    invaders.push({
      x: Math.random() * WINDOW_WIDTH,
      y: Math.random() * WINDOW_HEIGHT,
      dx: Math.random() * 100 - 50,
      dy: Math.random() * 100 - 50,      
    })
  }

}

function draw() {
  const frameLength = deltaTime / 1000;

  frameCounter++;
  fpsTimer += frameLength;
  if (fpsTimer > 1) {
    fpsTimer -= 1;
    fps = frameCounter;
    frameCounter = 0;
  }
  
  inputs();
  processes(frameLength);
  outputs();
}

function inputs() {
  if (keyIsDown(27)) {
    alert("Please press CTRL+W to close");
  }
}

function processes(frameLength) {

  for (let invader of invaders) {
    invader.x += invader.dx * frameLength;
    invader.y += invader.dy * frameLength;
    if (invader.x < -100) invader.x += WINDOW_WIDTH + 200;
    if (invader.y < -100) invader.y += WINDOW_HEIGHT + 200;
    if (invader.x > 100 + WINDOW_WIDTH) invader.x -= WINDOW_WIDTH + 200;
    if (invader.y > 100 + WINDOW_HEIGHT) invader.y -= WINDOW_HEIGHT + 200;
  }
}

function outputs() {
  background("black");

  for (let invader of invaders) {
    image(invaderSprite, invader.x - invaderSprite.width / 2, invader.y - invaderSprite.height / 2);
  }

  if (fps > 0) {
    fill("white");
    text("JavaScript (p5.js)   " + fps + " FPS", 20, 20);
  }
}
