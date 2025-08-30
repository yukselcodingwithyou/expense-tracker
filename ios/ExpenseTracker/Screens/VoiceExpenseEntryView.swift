import SwiftUI
import Speech
import AVFoundation

struct VoiceExpenseEntryView: View {
    @State private var isListening = false
    @State private var transcript = ""
    @State private var isProcessing = false
    @State private var speechRecognizer = SFSpeechRecognizer(locale: Locale(identifier: "en-US"))
    @State private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest?
    @State private var recognitionTask: SFSpeechRecognitionTask?
    @State private var audioEngine = AVAudioEngine()
    @State private var showingPermissionAlert = false
    @State private var permissionStatus: SFSpeechRecognizerAuthorizationStatus = .notDetermined
    
    let onVoiceProcessed: (String) -> Void
    @Environment(\.presentationMode) var presentationMode
    
    init(onVoiceProcessed: @escaping (String) -> Void) {
        self.onVoiceProcessed = onVoiceProcessed
    }
    
    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                // Status text
                Text(statusText)
                    .font(.title3)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                
                // Transcript display
                ScrollView {
                    VStack {
                        if transcript.isEmpty {
                            VStack(spacing: 16) {
                                Image(systemName: "quote.bubble")
                                    .font(.system(size: 48))
                                    .foregroundColor(.secondary)
                                
                                Text("Your spoken expense will appear here")
                                    .font(.headline)
                                    .foregroundColor(.secondary)
                                
                                Text("Example: \"I spent 25 dollars on coffee at Starbucks\"")
                                    .font(.body)
                                    .foregroundColor(.tertiary)
                                    .multilineTextAlignment(.center)
                            }
                            .frame(maxWidth: .infinity)
                            .frame(minHeight: 200)
                        } else {
                            Text(transcript)
                                .font(.body)
                                .padding()
                                .frame(maxWidth: .infinity, alignment: .leading)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color.secondary.opacity(0.1))
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(Color.secondary.opacity(0.3), lineWidth: 1)
                )
                
                Spacer()
                
                // Control buttons
                HStack(spacing: 32) {
                    // Microphone button
                    VoiceInputButton(
                        isListening: isListening,
                        isEnabled: !isProcessing && permissionStatus == .authorized,
                        onStartListening: startRecording,
                        onStopListening: stopRecording
                    )
                    
                    // Send button
                    if !transcript.isEmpty {
                        Button(action: processVoice) {
                            ZStack {
                                Circle()
                                    .fill(Color.blue)
                                    .frame(width: 56, height: 56)
                                
                                if isProcessing {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                } else {
                                    Image(systemName: "paperplane.fill")
                                        .font(.title2)
                                        .foregroundColor(.white)
                                }
                            }
                        }
                        .disabled(isProcessing)
                    }
                }
                .padding(.bottom, 32)
            }
            .padding()
            .navigationTitle("Voice Expense Entry")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        stopRecording()
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                
                if !transcript.isEmpty {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Clear") {
                            transcript = ""
                        }
                    }
                }
            }
        }
        .onAppear {
            requestSpeechPermission()
        }
        .onDisappear {
            stopRecording()
        }
        .alert("Microphone Permission Required", isPresented: $showingPermissionAlert) {
            Button("Settings") {
                if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(settingsURL)
                }
            }
            Button("Cancel") {
                presentationMode.wrappedValue.dismiss()
            }
        } message: {
            Text("This app needs microphone access to record your voice for expense entry. Please enable it in Settings.")
        }
    }
    
    private var statusText: String {
        if isProcessing {
            return "Processing your expense..."
        } else if isListening {
            return "Listening... Say your expense"
        } else if !transcript.isEmpty {
            return "Tap the send button to process"
        } else if permissionStatus != .authorized {
            return "Microphone permission required"
        } else {
            return "Tap the microphone to start recording"
        }
    }
    
    private func requestSpeechPermission() {
        SFSpeechRecognizer.requestAuthorization { status in
            DispatchQueue.main.async {
                self.permissionStatus = status
                switch status {
                case .authorized:
                    break
                case .denied, .restricted, .notDetermined:
                    self.showingPermissionAlert = true
                @unknown default:
                    self.showingPermissionAlert = true
                }
            }
        }
    }
    
    private func startRecording() {
        guard permissionStatus == .authorized else {
            showingPermissionAlert = true
            return
        }
        
        guard let speechRecognizer = speechRecognizer,
              speechRecognizer.isAvailable else { return }
        
        recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
        guard let recognitionRequest = recognitionRequest else { return }
        
        recognitionRequest.shouldReportPartialResults = true
        
        recognitionTask = speechRecognizer.recognitionTask(with: recognitionRequest) { [self] result, error in
            if let result = result {
                DispatchQueue.main.async {
                    self.transcript = result.bestTranscription.formattedString
                }
            }
            
            if error != nil || result?.isFinal == true {
                self.stopRecording()
            }
        }
        
        let inputNode = audioEngine.inputNode
        let recordingFormat = inputNode.outputFormat(forBus: 0)
        
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { buffer, _ in
            recognitionRequest.append(buffer)
        }
        
        audioEngine.prepare()
        
        do {
            try audioEngine.start()
            isListening = true
        } catch {
            print("Audio engine could not start: \(error)")
        }
    }
    
    private func stopRecording() {
        audioEngine.stop()
        audioEngine.inputNode.removeTap(onBus: 0)
        recognitionRequest?.endAudio()
        recognitionTask?.cancel()
        isListening = false
    }
    
    private func processVoice() {
        guard !transcript.isEmpty else { return }
        isProcessing = true
        onVoiceProcessed(transcript)
    }
}

struct VoiceInputButton: View {
    let isListening: Bool
    let isEnabled: Bool
    let onStartListening: () -> Void
    let onStopListening: () -> Void
    
    @State private var scale: CGFloat = 1.0
    
    var body: some View {
        Button(action: {
            if isListening {
                onStopListening()
            } else {
                onStartListening()
            }
        }) {
            ZStack {
                Circle()
                    .fill(isListening ? Color.red : Color.blue)
                    .frame(width: 80, height: 80)
                    .scaleEffect(scale)
                    .animation(
                        isListening ? 
                        Animation.easeInOut(duration: 1.0).repeatForever(autoreverses: true) :
                        Animation.default,
                        value: scale
                    )
                
                Image(systemName: isListening ? "mic.slash" : "mic")
                    .font(.title)
                    .foregroundColor(.white)
            }
        }
        .disabled(!isEnabled)
        .opacity(isEnabled ? 1.0 : 0.5)
        .onChange(of: isListening) { listening in
            scale = listening ? 1.2 : 1.0
        }
    }
}

#Preview {
    VoiceExpenseEntryView { spokenText in
        print("Voice processed: \(spokenText)")
    }
}