require 'rubygems'
require 'bundler'

begin
  Bundler.setup(:default, :development)
rescue Bundler::BundlerError => e
  $stderr.puts e.message
  $stderr.puts "Run `bundle install` to install missing gems"
  exit e.status_code
end

begin
  require "rspec/core/rake_task"

  desc "Run all examples"
  RSpec::Core::RakeTask.new(:spec) do |t|
    t.rspec_opts = %w[--color]
    t.pattern = 'spec/api/*_spec.rb'
  end
rescue LoadError
end

task :environment do
  ENV["RACK_ENV"] ||= 'development'
  require File.expand_path("../config/environment", __FILE__)
end

task :routes => :environment do
  Tab::API.routes.each do |route|
    p route
  end
end

begin
  require 'rubocop/rake_task'

  desc "Run Rubocop..whatever that does"
  Rubocop::RakeTask.new(:rubocop)

rescue LoadError
end

task default: [:rubocop, :spec]
