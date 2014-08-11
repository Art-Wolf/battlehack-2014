module Empowered
  module Models
    class Pledge
      include Mongoid::Document
      store_in collection: "pledges", database: "app28310256"
      belongs_to :issue

      field :id, type: String, description: "Pledge id."
      field :paypal_id, type: String, description: "PayPal id of pledge."
      field :issue_id, type: String, description: "Issue id of pledge."
      field :pledge_amount, type: Integer, description: "Amount of pledge."
      field :user_id, type: String, description: "User id of pledge."
    end
  end
end
