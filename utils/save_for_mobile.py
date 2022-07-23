import torch
from torch.utils.mobile_optimizer import optimize_for_mobile
from pathlib import Path
from external.superpoint.decoder import SuperPointDecoder
from external.superpoint.demo_superpoint import SuperPointNet


def save_for_mobile(module_class, state_dict_path=None, output_path=Path("output.ptl")):
    module = module_class()
    if state_dict_path is not None:
        module.load_state_dict(torch.load(state_dict_path))
    module.eval()

    scripted = torch.jit.script(module)
    optimized = optimize_for_mobile(scripted)

    print(f"========== Optimized code for {module_class.__name__} ==========")
    print(optimized.code)

    optimized._save_for_lite_interpreter(output_path.as_posix())


SUPERPOINTNET_PATH = "external/superpoint/superpoint_v1.pth"
ASSETS_DIR = "../lib/src/main/assets"
SUPERPOINTNET_ASSET_NAME = "superpoint.ptl"
SUPERPOINTDECODER_ASSET_NAME = "superpoint_decoder.ptl"

# Ensure assets directory exists
Path(ASSETS_DIR).mkdir(parents=True, exist_ok=True)

# Save SuperPoint
save_for_mobile(
    SuperPointNet,
    state_dict_path=Path(SUPERPOINTNET_PATH),
    output_path=Path(ASSETS_DIR, SUPERPOINTNET_ASSET_NAME))

# Save SuperPoint decoder
save_for_mobile(
    SuperPointDecoder,
    output_path=Path(ASSETS_DIR, SUPERPOINTDECODER_ASSET_NAME)
)
