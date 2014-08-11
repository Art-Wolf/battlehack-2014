require 'spec_helper'

describe Tab::API do
  include Rack::Test::Methods

  def app
    Tab::API
  end

  it "list" do
    get "/api/bar/list"
    last_response.status.should == 200
    last_response.body.should == { hello: "world" }.to_json
  end

end
