#!/usr/bin/env ruby

require 'simply_useful/cli'
include SimplyUseful::Cli

script_real_path = if File.symlink? __FILE__
  File.readlink(__FILE__)
else
  __FILE__
end

garden_dir = File.realpath(File.join(File.dirname(script_real_path), '..'))
local_dir = Dir.pwd

puts "Hey, I'm the garden hoe. I make using a new garden snapshot easier!"
puts "Garden directory: #{garden_dir}, local directory: #{local_dir}."

Dir.chdir(garden_dir) do
  run('sbt test publish')
end

Dir.chdir(local_dir) do
  run('sbt update')
end
