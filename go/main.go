package main

import (
	"fmt"
	"image"
	_ "image/png"
	"math/rand"
	"os"
	"time"

	"github.com/faiface/pixel"
	"github.com/faiface/pixel/pixelgl"
	"github.com/faiface/pixel/text"

	"io/ioutil"

	"github.com/golang/freetype/truetype"
	"golang.org/x/image/colornames"
	"golang.org/x/image/font"
)

const WINDOW_WIDTH = 1920
const WINDOW_HEIGHT = 1080

type Invader struct {
	x  float64
	y  float64
	dx float64
	dy float64
}

func (invader *Invader) Update() {
	invader.x += invader.dx * frameLength
	invader.y += invader.dy * frameLength
	if invader.x < -100 {
		invader.x += WINDOW_WIDTH + 200
	}
	if invader.y < -100 {
		invader.y += WINDOW_HEIGHT + 200
	}
	if invader.x > WINDOW_WIDTH + 100 {
		invader.x -= WINDOW_WIDTH + 200
	}
	if invader.y > WINDOW_HEIGHT+100 {
		invader.y -= WINDOW_HEIGHT + 200
	}
}

var invaders []Invader
var invaderSprite *pixel.Sprite

var window *pixelgl.Window	
var textRenderer *text.Text

var (
	frames        = 0
	second        = time.Tick(time.Second)
	frameLength   float64	
	quit          = false
	fps           int
)

func loadImageFile(path string) (image.Image, error) {
	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()
	img, _, err := image.Decode(file)
	if err != nil {
		return nil, err
	}
	return img, nil
}

func loadTTF(path string, size float64) (font.Face, error) {
	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	bytes, err := ioutil.ReadAll(file)
	if err != nil {
		return nil, err
	}

	font, err := truetype.Parse(bytes)
	if err != nil {
		return nil, err
	}

	return truetype.NewFace(font, &truetype.Options{
		Size:              size,
		GlyphCacheEntries: 1,
	}), nil
}

func initiate() {

	var initError error

	cfg := pixelgl.WindowConfig{
		Bounds:  pixel.R(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT),
		VSync:   true,
		Monitor: pixelgl.PrimaryMonitor(),		
		Title: "",
	}

	window, initError = pixelgl.NewWindow(cfg)
	if initError != nil {
		panic(initError)
	}

	textFace, initError := loadTTF("resources/font.ttf", 20)
	if initError != nil {
		panic(initError)
	}

	textAtlas := text.NewAtlas(textFace, text.ASCII)

	textRenderer = text.New(pixel.ZV, textAtlas)
	textRenderer.LineHeight = textAtlas.LineHeight()
	textRenderer.Color = colornames.White

	invaderImage, initError := loadImageFile("resources/sprite.png")
	if initError != nil {
		panic(initError)
	}
	invaderPic := pixel.PictureDataFromImage(invaderImage)
	invaderSprite = pixel.NewSprite(invaderPic, invaderPic.Bounds())

	rnd := rand.New(rand.NewSource(time.Now().UnixNano()))

	for i := 1; i <= 100; i++ {

		invaders = append(invaders,Invader{
			x:  rnd.Float64() * WINDOW_WIDTH, 
			y:  rnd.Float64() * WINDOW_HEIGHT,
			dx: rnd.Float64()*100 - 50,
			dy: rnd.Float64()*100 - 50,
		} )

	}

}

func inputs() {

	if window.Pressed(pixelgl.KeyEscape) {
		quit = true
	}
}

func processes() {

	for i := range invaders {
		invaders[i].Update();
	}

}

func outputs() {

	window.Clear(colornames.Black)
	textRenderer.Clear()

	for i := range invaders {
		matrix := pixel.IM.Moved(
		  pixel.Vec{
			X: invaders[i].x - invaderSprite.Picture().Bounds().W() / 2, 
			Y: invaders[i].y - invaderSprite.Picture().Bounds().H() / 2,
		  })
		invaderSprite.Draw(window, matrix)
	  }
	

	if fps > 0 {
		textRenderer.Dot = pixel.V(20, WINDOW_HEIGHT-20)
		textRenderer.WriteString(fmt.Sprintf("%s   %d %s", "Go (Pixel by Faiface)", fps, "FPS"))
		textRenderer.Draw(window, pixel.IM)
	}

	window.Update()

}

func game() {

	initiate()

	for !window.Closed() && !quit {

		frameStart := time.Now()
		frames++
		select {
		case <-second:
			fps = frames
			frames = 0
		default:
		}

		inputs()
		processes()
		outputs()

		frameLength = time.Since(frameStart).Seconds()

	}
}

func main() {

	pixelgl.Run(game)

}
