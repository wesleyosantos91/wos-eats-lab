import { 
  getAllKitchens, 
  getAllRestaurants, 
  createKitchen, 
  createRestaurant, 
  getKitchen, 
  getRestaurant,
  LOAD_PROFILES,
  PERFORMANCE_THRESHOLDS
} from '../utils/api-utils.js';

export let options = {
  stages: LOAD_PROFILES.moderate.stages,
  thresholds: PERFORMANCE_THRESHOLDS.load,
};

let createdKitchenIds = [];
let createdRestaurantIds = [];

export function setup() {
  console.log('Starting Load Test Setup...');
  
  // Create some initial data for load testing
  let kitchenData = { name: "Test Kitchen " + Date.now() };
  let restaurantData = { name: "Test Restaurant " + Date.now(), deliveryFee: 5.99 };
  
  return {
    sampleKitchen: kitchenData,
    sampleRestaurant: restaurantData
  };
}

export default function(data) {
  // Simulate realistic user behavior patterns
  const userBehavior = Math.random();
  
  if (userBehavior < 0.3) {
    // 30% - Browse kitchens and restaurants
    getAllKitchens();
    getAllRestaurants();
  } 
  else if (userBehavior < 0.5) {
    // 20% - Create new kitchen
    const kitchenData = { name: "Kitchen " + Date.now() };
    const createResult = createKitchen(kitchenData);
    if (createResult) {
      // Store ID for later operations (simplified for load test)
    }
  }
  else if (userBehavior < 0.7) {
    // 20% - Create new restaurant  
    const restaurantData = { name: "Restaurant " + Date.now(), deliveryFee: Math.random() * 5 + 3 };
    const createResult = createRestaurant(restaurantData);
    if (createResult) {
      // Store ID for later operations (simplified for load test)
    }
  }
  else if (userBehavior < 0.85) {
    // 15% - Get specific kitchen (simulate user clicking on kitchen)
    getAllKitchens(); // First get list
    // In real scenario, we'd pick an ID from the list
    // For load test, we simulate checking first few IDs
    const randomId = Math.floor(Math.random() * 10) + 1;
    getKitchen(randomId);
  }
  else {
    // 15% - Get specific restaurant (simulate user clicking on restaurant)
    getAllRestaurants(); // First get list
    // In real scenario, we'd pick an ID from the list
    // For load test, we simulate checking first few IDs
    const randomId = Math.floor(Math.random() * 10) + 1;
    getRestaurant(randomId);
  }
}

export function teardown(data) {
  console.log('Load Test completed');
  console.log('Summary: Tested realistic user interactions with Kitchen and Restaurant APIs');
}