#!/bin/bash

# Start Ollama in the background.
/bin/ollama serve &
# Record Process ID.
pid=$!

# Pause for Ollama to start.
sleep 5

echo "🔴 Retrieve Ollama models..."
ollama pull nomic-embed-text
ollama pull qwen:4b # getting out of memory for mistral:7b on my laptop
echo "🟢 Done!"

# Wait for Ollama process to finish.
wait $pid