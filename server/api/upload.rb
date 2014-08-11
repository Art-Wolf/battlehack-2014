require 'securerandom'

module Empowered
  class Upload < Grape::API
    version 'v1'
    format :json

    resource :upload do
      resource :list do

        desc "Get a list of uploads."
        get do
          upload = Empowered::Models::Upload.all()
          error! "Not Found", 404 unless upload
          upload.as_json
        end
      end

      desc "Create an upload.", params: Empowered::Models::Upload.fields.dup.tap { |fields| fields.delete("_id") }
      post do
        file_name = params[:image_file][:filename]
        file_size = params[:image_file][:tempfile].size
        temp_file = params[:image_file][:tempfile]
	      file_path = "public/images/#{SecureRandom.urlsafe_base64(20)}.png"

        FileUtils.cp(temp_file, file_path)

        upload = Empowered::Models::Upload.create!( {
          file_name: file_name,
          file_size: file_size,
          file_path: file_path
        } )

        # return just the path to the upload as a String without `public/`
        # ** returns as escaped JSON, e.g., "\"path/to/file\"" **
        file_path[7..-1]
      end

      desc "Retrieve an upload by id."
      params do
        requires :id, type: String, desc: "Upload id."
      end
      route_param :id do
        get do
          upload = Empowered::Models::Upload.where({ id: params[:id] })
          error! "Not Found", 404 unless upload
          upload.as_json
        end
      end

      desc "Delete an upload by id.", params: { "id" => { description: "Upload id."}, required: true }
      delete do
        upload = Empowered::Models::Upload.find_by({ id: params[:id] })
        error! "Not Found", 404 unless upload
        upload.destroy
        upload.as_json
      end
    end
  end
end
