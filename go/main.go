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

const screenWidth = 1920
const screenHeight = 1080

type Invader struct {
	x       float64
	y       float64
	dx      float64
	dy      float64
	radius  float64
	expired bool
}

var (
	frames        = 0
	second        = time.Tick(time.Second)
	window        *pixelgl.Window
	frameLength   float64
	invaders      []Invader
	invaderPic    pixel.Picture
	invaderSprite *pixel.Sprite
	quit          = false
	textRenderer  *text.Text
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
		Bounds:  pixel.R(0, 0, screenWidth, screenHeight),
		VSync:   true,
		Monitor: pixelgl.PrimaryMonitor(),
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
	invaderPic = pixel.PictureDataFromImage(invaderImage)
	invaderSprite = pixel.NewSprite(invaderPic, invaderPic.Bounds())

	r := rand.New(rand.NewSource(time.Now().UnixNano()))

	for i := 1; i <= 100; i++ {

		invader := Invader{
			x:      r.Float64() * screenWidth,
			y:      r.Float64() * screenHeight,
			dx:     r.Float64()*100 - 50,
			dy:     r.Float64()*100 - 50,
			radius: 32,
		}

		invaders = append(invaders, invader)

	}

}

func game() {

	initiate()

	for !window.Closed() && !quit {

		frameStart := time.Now()
		textRenderer.Clear()

		if window.Pressed(pixelgl.KeyEscape) {
			quit = true
		}

		for i := range invaders {

			invaders[i].x += invaders[i].dx * frameLength
			invaders[i].y += invaders[i].dy * frameLength

			if invaders[i].x < -100 {
				invaders[i].x += screenWidth + 200
			}
			if invaders[i].y < -100 {
				invaders[i].y += screenHeight + 200
			}
			if invaders[i].x > screenWidth+100 {
				invaders[i].x -= screenWidth + 200
			}
			if invaders[i].y > screenHeight+100 {
				invaders[i].y -= screenHeight + 200
			}

		}

		window.Clear(colornames.Black)

		for i := range invaders {

			matrix := pixel.IM.Moved(pixel.Vec{X: invaders[i].x, Y: invaders[i].y})

			invaderSprite.Draw(window, matrix)

		}

		frames++
		select {
		case <-second:
			fps = frames
			frames = 0
		default:
		}

		if fps > 0 {
			textRenderer.Dot = pixel.V(20, screenHeight-20)
			textRenderer.WriteString(fmt.Sprintf("%s   %d %s", "Go (Pixel by Faiface)", fps, "FPS"))
			textRenderer.Draw(window, pixel.IM)
		}

		window.Update()

		frameLength = time.Since(frameStart).Seconds()

	}
}

func main() {

	pixelgl.Run(game)

}
