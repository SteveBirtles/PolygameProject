WINDOW_WIDTH = 1920
WINDOW_HEIGHT = 1080

function newInvader(x, y, dx, dy, r)
    local invader = {}
    invader.x = x
    invader.y = y
    invader.dx = dx
    invader.dy = dy
    invader.r = r
    return invader
end

invaders = {}

function love.load()

    love.window.setMode(WINDOW_WIDTH, WINDOW_HEIGHT, {
        fullscreen = true
    })

    image = love.graphics.newImage("resources/sprite.png")

    for i = 1, 100 do
        local x = math.random(0, WINDOW_WIDTH)
        local y = math.random(0, WINDOW_HEIGHT)
        local dx = math.random(-50, 50)
        local dy = math.random(-50, 50)
        table.insert(invaders, newInvader(x, y, dx, dy, 32))
    end

end

timer = 0
fps = 0
frames = 0

function love.update(dt)

    timer = timer + dt
    frames = frames + 1
    if timer > 1.0 then
        fps = frames
        frames = 0
        timer = timer - 1
    end

    if love.keyboard.isDown("escape") then
        love.event.quit()
    end

    for _, invader in ipairs(invaders) do
        invader.x = invader.x + invader.dx * dt
        invader.y = invader.y + invader.dy * dt
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

function love.draw()

    for _, invader in ipairs(invaders) do
        love.graphics.draw(image, invader.x, invader.y)
    end

    if fps > 0 then
        love.graphics.print("Lua (Love2D)   " + fps .. " FPS", 10, 10)
    end

end
