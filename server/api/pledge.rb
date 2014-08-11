require 'open-uri'
require 'json'

module Empowered
  class Pledge < Grape::API
    version 'v1'
    format :json

    resource :pledge do
      resource :list do

        desc "Get a list of pledges."
        get do
          pledge = Empowered::Models::Pledge.all()
          error! "Not Found", 404 unless pledge
          pledge.as_json
        end
      end

      desc "Create a pledge.", params: Empowered::Models::Pledge.fields.dup.tap { |fields| fields.delete("_id") }
      post do
        pledge = Empowered::Models::Pledge.create!( {
                  paypal_id: params[:paypal_id],
                  issue_id: params[:issue_id],
                  pledge_amount: params[:pledge_amount],
                  user_id: params[:user_id]
                } )

        issue = Empowered::Models::Issue.where({id: params[:issue_id]})

        subject = "#{pledge.user_id} just pledged $#{pledge.pledge_amount}" # on #{issue['title']}!"
        recipient = 'raevyn_@hotmail.com' #issue['email']
        body = "Woohoo!\nLove, empowered-local-bot"

        Pony.mail(
          :from => 'empowered@locals.org',
          :to => recipient,
          :subject => subject,
          :body => body,
          :via => :smtp,
          :via_options => {
            :address        => 'smtp.sendgrid.net',
            :port           => '587',
            :user_name      => ENV['SENDGRID_USERNAME'],
            :password       => ENV['SENDGRID_PASSWORD'],
            :authentication => :plain,
            :domain         => "empowered-locals.herokuapp.com"
          }
        )

        pledge.as_json
      end

      desc "Retrieve a pledge by id."
      params do
        requires :id, type: String, desc: "Pledge id."
      end
      route_param :id do
        get do
          pledge = Empowered::Models::Pledge.where({ id: params[:id] })
          error! "Not Found", 404 unless pledge
          pledge.as_json
        end
      end

      desc "Update a pledge by id.", params: Empowered::Models::Pledge.fields.merge( "id" => {description: "Pledge id.", required: true})
      put do
        pledge = Empowered::Models::Pledge.find_by({id: params[:id]})
        error! "Not Found", 404 unless pledge
        values = {}
        values[:paypal_id] = params[:paypal_id] if params.key?(:paypal_id)
        values[:issue_id] = params[:issue_id] if params.key?(:issue_id)
        values[:pledge_amount] = params[:pledge_amount] if params.key?(:pledge_amount)
        values[:user_id] = params[:user_id] if params.key?(:user_id)
        pledge.update_attributes!(values)
        pledge.as_json
      end

      desc "Delete a pledge by id.", params: { "id" => { description: "Pledge id."}, required: true }
      delete do
        pledge = Empowered::Models::Pledge.find_by({ id: params[:id] })
        error! "Not Found", 404 unless pledge
        pledge.destroy
        pledge.as_json
      end
    end
  end
end
