import torch
from pathlib import Path
from torch.utils.mobile_optimizer import optimize_for_mobile
from external.superpoint.decoder import SuperPointDecoder
from external.superpoint.demo_superpoint import SuperPointNet


def save_for_mobile(
        module_class,
        output_path,
        state_dict_path=None,
        modules_to_fuse=None,
        calibrate=None,
        optimize=False
):
    print(f"Saving {module_class.__name__}")

    module = module_class()
    if state_dict_path is not None:
        print(f"- Loading state dict: {state_dict_path}")
        module.load_state_dict(torch.load(state_dict_path.as_posix()))
    module.eval()

    # Fuse
    if modules_to_fuse is not None:
        print("- Fusing")
        torch.quantization.fuse_modules(module, modules_to_fuse=modules_to_fuse, inplace=True)

    # Quantize
    if calibrate is not None:
        print("- Quantizing")
        module.qconfig = torch.quantization.get_default_qconfig("qnnpack")
        torch.quantization.prepare(module, inplace=True)
        calibrate(module)
        torch.quantization.convert(module, inplace=True)

    # Script
    scripted = torch.jit.script(module)

    # Optimize for mobile
    if optimize:
        print("- Optimizing")
        scripted = optimize_for_mobile(scripted)

    # Save
    scripted._save_for_lite_interpreter(output_path.as_posix())

    print(f"- Saved {module_class.__name__} to {output_path}")


ASSETS_DIR = "../lib/src/main/assets"
SUPERPOINTNET_ASSET = "superpoint.ptl"
SUPERPOINTNET_PATH = "external/superpoint/superpoint_v1.pth"
SUPERPOINTDECODER_ASSET = "superpoint_decoder.ptl"

# Ensure assets directory exists
Path(ASSETS_DIR).mkdir(parents=True, exist_ok=True)

# Save SuperPoint
save_for_mobile(
    SuperPointNet,
    output_path=Path(ASSETS_DIR, SUPERPOINTNET_ASSET),
    state_dict_path=Path(SUPERPOINTNET_PATH),
    modules_to_fuse=[["conv1a", "relu1a"], ["conv1b", "relu1b"],
                     ["conv2a", "relu2a"], ["conv2b", "relu2b"],
                     ["conv3a", "relu3a"], ["conv3b", "relu3b"],
                     ["conv4a", "relu4a"], ["conv4b", "relu4b"],
                     ["convPa", "reluPa"], ["convDa", "reluDa"]]
)

# Save SuperPoint decoder
save_for_mobile(
    SuperPointDecoder,
    output_path=Path(ASSETS_DIR, SUPERPOINTDECODER_ASSET),
    optimize=True
)
