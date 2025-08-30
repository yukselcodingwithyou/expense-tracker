import SwiftUI
import AVFoundation
import UIKit

struct ReceiptCameraView: UIViewControllerRepresentable {
    @Binding var capturedImage: UIImage?
    @Environment(\.presentationMode) var presentationMode
    
    func makeUIViewController(context: Context) -> ReceiptCameraViewController {
        let controller = ReceiptCameraViewController()
        controller.delegate = context.coordinator
        return controller
    }
    
    func updateUIViewController(_ uiViewController: ReceiptCameraViewController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, ReceiptCameraDelegate {
        let parent: ReceiptCameraView
        
        init(_ parent: ReceiptCameraView) {
            self.parent = parent
        }
        
        func didCaptureImage(_ image: UIImage) {
            parent.capturedImage = image
            parent.presentationMode.wrappedValue.dismiss()
        }
        
        func didCancel() {
            parent.presentationMode.wrappedValue.dismiss()
        }
    }
}

protocol ReceiptCameraDelegate: AnyObject {
    func didCaptureImage(_ image: UIImage)
    func didCancel()
}

class ReceiptCameraViewController: UIViewController {
    weak var delegate: ReceiptCameraDelegate?
    private var captureSession: AVCaptureSession!
    private var previewLayer: AVCaptureVideoPreviewLayer!
    private var photoOutput: AVCapturePhotoOutput!
    private var flashButton: UIButton!
    private var isFlashOn = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupCamera()
        setupUI()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if captureSession?.isRunning == false {
            DispatchQueue.global(qos: .userInitiated).async {
                self.captureSession.startRunning()
            }
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if captureSession?.isRunning == true {
            DispatchQueue.global(qos: .userInitiated).async {
                self.captureSession.stopRunning()
            }
        }
    }
    
    private func setupCamera() {
        captureSession = AVCaptureSession()
        captureSession.sessionPreset = .photo
        
        guard let backCamera = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back) else {
            showError("Unable to access camera")
            return
        }
        
        do {
            let input = try AVCaptureDeviceInput(device: backCamera)
            captureSession.addInput(input)
            
            photoOutput = AVCapturePhotoOutput()
            captureSession.addOutput(photoOutput)
            
            previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
            previewLayer.videoGravity = .resizeAspectFill
            previewLayer.frame = view.bounds
            view.layer.addSublayer(previewLayer)
            
            DispatchQueue.global(qos: .userInitiated).async {
                self.captureSession.startRunning()
            }
        } catch {
            showError("Error setting up camera: \(error.localizedDescription)")
        }
    }
    
    private func setupUI() {
        // Back button
        let backButton = UIButton(type: .system)
        backButton.setImage(UIImage(systemName: "arrow.left"), for: .normal)
        backButton.tintColor = .white
        backButton.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        backButton.layer.cornerRadius = 25
        backButton.addTarget(self, action: #selector(backButtonTapped), for: .touchUpInside)
        backButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(backButton)
        
        // Flash button
        flashButton = UIButton(type: .system)
        flashButton.setImage(UIImage(systemName: "bolt.slash"), for: .normal)
        flashButton.tintColor = .white
        flashButton.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        flashButton.layer.cornerRadius = 25
        flashButton.addTarget(self, action: #selector(toggleFlash), for: .touchUpInside)
        flashButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(flashButton)
        
        // Capture button
        let captureButton = UIButton(type: .system)
        captureButton.setImage(UIImage(systemName: "camera"), for: .normal)
        captureButton.tintColor = .white
        captureButton.backgroundColor = UIColor.systemBlue
        captureButton.layer.cornerRadius = 40
        captureButton.addTarget(self, action: #selector(capturePhoto), for: .touchUpInside)
        captureButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(captureButton)
        
        // Receipt frame guide
        let frameGuide = ReceiptFrameGuideView()
        frameGuide.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(frameGuide)
        
        // Instructions label
        let instructionsLabel = UILabel()
        instructionsLabel.text = "Align receipt within the frame"
        instructionsLabel.textColor = .white
        instructionsLabel.textAlignment = .center
        instructionsLabel.backgroundColor = UIColor.black.withAlphaComponent(0.7)
        instructionsLabel.layer.cornerRadius = 8
        instructionsLabel.layer.masksToBounds = true
        instructionsLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(instructionsLabel)
        
        NSLayoutConstraint.activate([
            // Back button
            backButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 16),
            backButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 16),
            backButton.widthAnchor.constraint(equalToConstant: 50),
            backButton.heightAnchor.constraint(equalToConstant: 50),
            
            // Flash button
            flashButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 16),
            flashButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -16),
            flashButton.widthAnchor.constraint(equalToConstant: 50),
            flashButton.heightAnchor.constraint(equalToConstant: 50),
            
            // Frame guide
            frameGuide.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            frameGuide.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            frameGuide.widthAnchor.constraint(equalTo: view.widthAnchor, multiplier: 0.8),
            frameGuide.heightAnchor.constraint(equalTo: frameGuide.widthAnchor, multiplier: 1.3),
            
            // Instructions label
            instructionsLabel.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 80),
            instructionsLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            instructionsLabel.widthAnchor.constraint(lessThanOrEqualTo: view.widthAnchor, constant: -32),
            instructionsLabel.heightAnchor.constraint(equalToConstant: 40),
            
            // Capture button
            captureButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -50),
            captureButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            captureButton.widthAnchor.constraint(equalToConstant: 80),
            captureButton.heightAnchor.constraint(equalToConstant: 80)
        ])
    }
    
    @objc private func backButtonTapped() {
        delegate?.didCancel()
    }
    
    @objc private func toggleFlash() {
        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back),
              device.hasTorch else { return }
        
        do {
            try device.lockForConfiguration()
            isFlashOn.toggle()
            
            if isFlashOn {
                try device.setTorchModeOn(level: 1.0)
                flashButton.setImage(UIImage(systemName: "bolt"), for: .normal)
            } else {
                device.torchMode = .off
                flashButton.setImage(UIImage(systemName: "bolt.slash"), for: .normal)
            }
            
            device.unlockForConfiguration()
        } catch {
            print("Flash error: \(error)")
        }
    }
    
    @objc private func capturePhoto() {
        let settings = AVCapturePhotoSettings()
        if isFlashOn {
            settings.flashMode = .on
        }
        photoOutput.capturePhoto(with: settings, delegate: self)
    }
    
    private func showError(_ message: String) {
        let alert = UIAlertController(title: "Error", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default) { _ in
            self.delegate?.didCancel()
        })
        present(alert, animated: true)
    }
}

extension ReceiptCameraViewController: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        guard let imageData = photo.fileDataRepresentation(),
              let image = UIImage(data: imageData) else {
            showError("Failed to capture image")
            return
        }
        
        // Rotate image if needed
        let orientedImage = image.fixOrientation()
        delegate?.didCaptureImage(orientedImage)
    }
}

class ReceiptFrameGuideView: UIView {
    override func draw(_ rect: CGRect) {
        guard let context = UIGraphicsGetCurrentContext() else { return }
        
        context.setStrokeColor(UIColor.white.cgColor)
        context.setLineWidth(3.0)
        
        let cornerLength: CGFloat = 20
        
        // Top-left corner
        context.move(to: CGPoint(x: 0, y: cornerLength))
        context.addLine(to: CGPoint(x: 0, y: 0))
        context.addLine(to: CGPoint(x: cornerLength, y: 0))
        
        // Top-right corner
        context.move(to: CGPoint(x: rect.width - cornerLength, y: 0))
        context.addLine(to: CGPoint(x: rect.width, y: 0))
        context.addLine(to: CGPoint(x: rect.width, y: cornerLength))
        
        // Bottom-left corner
        context.move(to: CGPoint(x: 0, y: rect.height - cornerLength))
        context.addLine(to: CGPoint(x: 0, y: rect.height))
        context.addLine(to: CGPoint(x: cornerLength, y: rect.height))
        
        // Bottom-right corner
        context.move(to: CGPoint(x: rect.width - cornerLength, y: rect.height))
        context.addLine(to: CGPoint(x: rect.width, y: rect.height))
        context.addLine(to: CGPoint(x: rect.width, y: rect.height - cornerLength))
        
        context.strokePath()
    }
}

extension UIImage {
    func fixOrientation() -> UIImage {
        if imageOrientation == .up {
            return self
        }
        
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        draw(in: CGRect(origin: .zero, size: size))
        let normalizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return normalizedImage ?? self
    }
}