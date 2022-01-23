"use strict";
p5.disableFriendlyErrors = true;

let invaderSprite;
let invaders = [];
let fps = 0;
let frameCounter = 0;
let fpsTimer = 0;

function preload() {
  invaderSprite = loadImage("resources/sprite.png");
}

function setup() {
  createCanvas(windowWidth, windowHeight, P2D);
  frameRate(60);

  for (let i = 0; i < 100; i++) {
    invaders.push({
      x: Math.floor(Math.random() * windowWidth),
      y: Math.floor(Math.random() * windowHeight),
      dx: Math.floor(Math.random() * 100) - 50,
      dy: Math.floor(Math.random() * 100) - 50,
      r: 32,
      expired: false,
    })
  }

}

function windowResized() {
  resizeCanvas(windowWidth, windowHeight);
}

function draw() {
  inputs();
  processes();
  outputs();
}

function inputs() {
}

function processes() {

  const frameLength = deltaTime / 1000;

  frameCounter++;
  fpsTimer += frameLength;
  if (fpsTimer > 1) {
    fpsTimer -= 1;
    fps = frameCounter;
    frameCounter = 0;
  }

  for (let invader of invaders) {
    invader.x += invader.dx * frameLength;
    invader.y += invader.dy * frameLength;
    if (invader.x < -100) invader.x += windowWidth + 200;
    if (invader.y < -100) invader.y += windowHeight + 200;
    if (invader.x > 100 + windowWidth) invader.x -= windowWidth + 200;
    if (invader.y > 100 + windowHeight) invader.y -= windowHeight + 200;

  }
}

function outputs() {
  background("black");

  for (let invader of invaders) {
    image(invaderSprite, invader.x, invader.y);
  }

  if (fps > 0) {
    fill("white");
    text("JavaScript (p5.js)   " + fps + " FPS", 20, 20);
  }
}
