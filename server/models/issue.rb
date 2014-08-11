module Empowered
  module Models
    class Issue
      include Mongoid::Document
      store_in collection: "issues", database: "app28310256"
      has_many :pledges

      field :id, type: String, description: "Issue id."
      field :title, type: String, description: "Title of issue."
      field :submitted_by, type: String, description: "Name of person who submitted issue."
      field :email, type: String, description: "Email of submitter."
      field :description, type: String, description: "Description of issue."
      field :image_url, type: String, description: "Image URL of issue."
      field :latitude, type: String, description: "Latitude of issue's location."
      field :longitude, type: String, description: "Longitude of issue's location."
    end
  end
end
