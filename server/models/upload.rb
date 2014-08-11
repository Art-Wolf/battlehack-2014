module Empowered
  module Models
    class Upload
      include Mongoid::Document
      store_in collection: "uploads", database: "app28310256"

      field :id, type: String, description: "Upload id."
      field :file_name, type: String, description: "Filename of upload."
      field :file_size, type: Integer, description: "File size of upload."
      field :file_path, type: String, description: "Filepath of upload."
    end
  end
end
