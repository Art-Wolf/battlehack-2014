module Empowered
  class API < Grape::API
    prefix 'api'
    format :json
    mount ::Empowered::Issue
    mount ::Empowered::Upload
    mount ::Empowered::Pledge
    add_swagger_documentation api_version: 'v1'
  end
end
