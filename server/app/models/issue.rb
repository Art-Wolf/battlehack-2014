class Issue
  include Mongoid::Document

  field :id, type => String
  field :title, type => String
  field :submitted_by, type => String
  field :description, type => String
  field :image_url, type => String
  field :latitude, type => String
  field :longitude, type => String
end
