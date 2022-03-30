WINDOW_WIDTH = 1920
WINDOW_HEIGHT = 1080

invaders = {}

function newInvader(x, y, dx, dy) 

    return {
        x = x,
        y = y,
        dx = dx,
        dy = dy
    }

end


function love.load()

    love.window.setMode(WINDOW_WIDTH, WINDOW_HEIGHT, {
        fullscreen = true
    })

    invaderImage = love.graphics.newImage("resources/sprite.png")

    for i = 1, 100 do  
        table.insert(invaders, newInvader(
            math.random(0, WINDOW_WIDTH),
            math.random(0, WINDOW_HEIGHT),
            math.random(-50, 50),
            math.random(-50, 50)
        ))
      end
      
end

timer = 0
fps = 0
frames = 0

function inputs()
    
    if love.keyboard.isDown("escape") then
        love.event.quit()
    end

end

function processes(frameLength) 

    for _, invader in ipairs(invaders) do
        invader.x = invader.x + invader.dx * frameLength
        invader.y = invader.y + invader.dy * frameLength
        if invader.x < -100 then
            invader.x = invader.x + WINDOW_WIDTH + 200
        end
        if invader.y < -100 then
            invader.y = invader.y + WINDOW_HEIGHT + 200
        end
        if invader.x > 100 + WINDOW_WIDTH then
            invader.x = invader.x - (WINDOW_WIDTH + 200)
        end
        if invader.y > 100 + WINDOW_HEIGHT then
            invader.y = invader.y - (WINDOW_HEIGHT + 200)
        end
    end

end

function love.update(frameLength)

    timer = timer + frameLength
    frames = frames + 1
    if timer > 1.0 then
        fps = frames
        frames = 0
        timer = timer - 1
    end

    inputs()
    processes(frameLength)

end

function love.draw()

    for _, invader in ipairs(invaders) do
        love.graphics.draw(invaderImage, invader.x - invaderImage:getWidth() / 2, invader.y - invaderImage:getHeight() / 2)
    end

    if fps > 0 then
        love.graphics.print("Lua (Love2D)   " .. fps .. " FPS", 10, 10)
    end

end
