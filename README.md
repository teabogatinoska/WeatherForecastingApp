Cloud-based application that aggregates weather data from multiple weather forecasting APIs to improve forecast accuracy. The application utilizes a microservices architecture, enabling optimal cloud deployment, scalability, and flexibility. The primary objective was to enhance the reliability of weather forecasts by combining data from various sources, ensuring more accurate and comprehensive predictions. This project focused on creating a modular and maintainable solution for real-time data aggregation and processing in a cloud environment.

The system consists of several distinct collaborating microservices. The key microservices are:
* **Weather Fetcher Microservice**: This microservice is responsible for retrieving weather data
from multiple external Apis. It collects raw weather data for user-requested locations and
passes this data for further processing.
*  **Weather Processor Microservice**: This microservice aggregates and processes the data received from the Weather Fetcher.
It handles discrepancies between API responses and calculates a unified, more accurate forecast. In
the end it prepares the data for the other microservice that needs to receive it.
*  **Weather Presenter Microservice**: This microservice formats the aggregated and processed
weather data into a user-friendly format and delivers it to the frontend of the system. It
ensures that users can view weather forecasts for the location they request, including
detailed hourly and daily forecasts.
* **Weather Alert Microservice**: This microservice monitors weather alert data and sends alert
notifications based on user preferences. It listens to location updates from the user’s
favorites and periodically checks external alert APIs to notify users about important
incoming weather changes.
* **Authentication Microservice**: Handles user registration, authentication and authorization
