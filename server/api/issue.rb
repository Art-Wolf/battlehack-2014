module Empowered
  class Issue < Grape::API
    version 'v1'
    format :json

    resource :issue do
      resource :list do

        desc "Get a list of issues."
        get do
          issue = Empowered::Models::Issue.all()
          error! "Not Found", 404 unless issue
          issue.as_json
        end
      end

      resource :by do
        desc "Retrieve an issue based on the submitter's name"
        params do
          requires :submitted_by, type: String, desc: "Submitters' names."
        end
        route_param :submitted_by do
          get do
            issue = Empowered::Models::Issue.where({submitted_by: params[:submitted_by]})
            error! "Not Found", 404 unless issue
            issue.as_json
          end
        end
      end

      desc "Create an issue" , params: Empowered::Models::Issue.fields.dup.tap {|fields| fields.delete("_id") }
      post do
        issue = Empowered::Models::Issue.create!( {
          title: params[:title],
          submitted_by: params[:submitted_by],
          email: 'raevyn_@hotmail.com', #params[:email],
          description: params[:description],
          image_url: params[:image_url],
          latitude: params[:latitude],
          longitude: params[:longitude]
        } )

        # Returns the issue id as a JSON escaped String
        issue.id.to_s
      end

      desc "Retrieve an issue based on the ID"
      params do
        requires :id, type: String, desc: "Issue ID."
      end
      route_param :id do
        get do
          issue = Empowered::Models::Issue.where({id: params[:id]})
          error! "Not Found", 404 unless issue
          issue.as_json
        end
      end

      desc "Update an issue by id.", params: Empowered::Models::Issue.fields.merge( "id" => {description: "Issue id.", required: true})
      put do
        issue = Empowered::Models::Issue.find_by({id: params[:id]})
        error! "Not Found", 404 unless issue
        values = {}
        values[:id] = params[:id] if params.key?(:id)
        values[:title] = params[:title] if params.key?(:title)
        values[:submitted_by] = params[:submitted_by] if params.key?(:submitted_by)
        values[:description] = params[:description] if params.key?(:description)
        values[:image_url] = params[:image_url] if params.key?(:image_url)
        values[:latitude] = params[:latitude] if params.key?(:latitude)
        values[:longitude] = params[:longitude] if params.key?(:longitude)
        issue.update_attributes!(values)
        issue.as_json
      end

      desc "Delete an issue by id.", params: { "id" => {description: "Issue id.", required: true}}
      delete do
        issue = Empowered::Models::Issue.find_by({id: params[:id]})
        error! "Not Found", 404 unless issue
        issue.destroy
        issue.as_json
      end

      # Endpoint for pledge information on a specific issue

      desc "Get a list of pledge info on an issue given an issue id"
      params do
        requires :id, type: String, desc: "Issue id."
      end
      route_param :id do
        get "pledges" do
          issue = Empowered::Models::Issue.where({id: params[:id]})
          error! "Issue Not Found", 404 unless issue
          pledges = issue.first.pledges
          {
            pledges: pledges,
            pledge_count: pledges.size,
            pledge_amounts: pledges.map(&:pledge_amount),
            pledge_total: pledges.map(&:pledge_amount).reduce(:+),
            user_ids: pledges.map(&:user_id)
          }.as_json
        end
      end

      # Endpoint for issue information given
      # latitude, longitude, and a mile radius

      desc "Get a list of issues within an n-mile radius of a location"
      params do
        requires :latitude, :longitude, :mile_radius,
        desc: "Latitude, longitude, and/or mile radius"
      end
      post "nearby" do
        issues = Empowered::Models::Issue.all.select do |issue|
          (issue.latitude.to_f - params[:latitude].to_f)**2 + (issue.longitude.to_f - params[:longitude].to_f)**2 < params[:mile_radius].to_f ** 2
        end
        error! "Issue Not Found", 404 unless issues
        {
          issues: issues,
          issue_count: issues.size
        }.as_json
      end
    end
  end
end
