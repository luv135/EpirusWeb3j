# To build a Dockerized version execute: `docker build -t web3app .`
# To run the generated image execute: `docker run --env EPIRUS_LOGIN_TOKEN="<your login token>" web3app`
FROM adoptopenjdk/openjdk11
RUN mkdir /opt/app
COPY . /opt/app
WORKDIR /opt/app
RUN curl -L get.epirus.io | sh
ENTRYPOINT ["/root/.epirus/epirus", "run", "rinkeby"]
