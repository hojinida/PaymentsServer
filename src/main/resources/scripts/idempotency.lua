local key = KEYS[1]
local processingFlag = ARGV[1]
local ttl = ARGV[2]

local existing = redis.call('SET', key, processingFlag, 'NX', 'GET', 'EX', ttl)

if existing then
    return existing
else
    return 'ACQUIRED'
end
